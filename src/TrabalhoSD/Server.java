package TrabalhoSD;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.String.valueOf;

class sistemaMapaRecompensas {
    private Mapa mapa;
    private SistemaRecompensas sistemaDeRecompenas;
    private HashMap<String, Tuple> codigosReserva = new HashMap<>();
    private HashMap<String,Long> startTimeMap = new HashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    public sistemaMapaRecompensas(){
        this.mapa= new Mapa();
        ReentrantReadWriteLock l = mapa.getLock();
        this.sistemaDeRecompenas = new SistemaRecompensas(l);
    }


    public Mapa getMapa(){
        return this.mapa;
    }

    public SistemaRecompensas getSistemaDeRecompensas() { return this.sistemaDeRecompenas;}

    public String reservaTrot(Tuple tuplo){ //TESTAR. O Cliente precisa de dar parse à String para saber o codigo de reserva/failure e a localização.
                                            // Alterar o stringbuilder para facilitar o parse se necessário.
        Tuple local =null;
        try {
            lock.lock();
            local = mapa.find_Perto(2, tuplo.getX(), tuplo.getY());
            if (!local.existeTuplo()) return "FAILURE";
            mapa.removeTrotinete(local.getX(), local.getY());
        }finally {
            lock.unlock();
        }

        String code = generateRandomCode();

        //no minimo um lock aqui - 1
        codigosReserva.put(code,local);
        //no minimo um lock aqui - 1;

        StringBuilder resposta = new StringBuilder();
        resposta.append("Código de Reserva: ")
                .append(code)
                .append("\n")
                .append(local.toString());

        long startTime = System.currentTimeMillis();
        startTimeMap.put(code,startTime);

        this.sistemaDeRecompenas.signalSistema();

        return resposta.toString();
    }

    public Double estacionaTrot(String code, Tuple localFinal){ //TESTAR
        long endTime = System.currentTimeMillis();

        if(!codigosReserva.containsKey(code)) return -1.0;

        Tuple localInicial = codigosReserva.get(code);
        Long startTime = startTimeMap.get(code);

        double custo = calculaViagem(startTime, endTime, localInicial, localFinal);
        //verificar recompensa



        try {
            lock.lock(); //lock questionavel
            mapa.addTrotinete(localFinal.getX(), localFinal.getY());
        } finally{lock.unlock();}

        this.sistemaDeRecompenas.signalSistema();

        return custo;
    }

    public double calculaViagem(Long inicioTime,Long finalTime,Tuple localInicial, Tuple localFinal){ //meti uma função para calcular o custo atoa. TESTAR
        double custo =0;

        double distanciaViagem = localFinal.calculaDistancia(localInicial);
        long elapsedTime = inicioTime - finalTime;

        custo = distanciaViagem * elapsedTime *0.1;

        return custo;
    }

    public static String generateRandomCode() { //TESTAR
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            code.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return code.toString();
    }
}


class ServerWorker implements Runnable{
    private sistemaMapaRecompensas sMR;
    private Socket s;
    private final TaggedConnection c;
    private Login login;

    public ServerWorker(sistemaMapaRecompensas sistema, Socket s, TaggedConnection c, Login login) throws Exception {
        this.sMR= sistema;
        this.s = s;
        this.c = c;
        this.login = login;
    }

    public void run() {

        try (c) {
            while(true) {
                TaggedConnection.Frame frame = c.receive();
                int tag = frame.tag;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos);

                sMR.getMapa().printMatrix();

                switch (tag) {
                    case 1: // Registar Conta
                        TaggedConnection.Frame pass = c.receive();

                        if(this.login.register(new String(frame.data),new String(pass.data))) c.send(frame.tag, new byte[]{(byte) 1});
                        else c.send(frame.tag, new byte[]{(byte) 0});

                        break;
                    case 2: // Efetuar Login
                        TaggedConnection.Frame password = c.receive();

                        if(this.login.tentativaLogin(new String(frame.data),new String(password.data))) c.send(frame.tag, new byte[]{(byte) 1});
                        else c.send(frame.tag, new byte[]{(byte) 0});

                        break;
                    case 3: // Cliente quer uma listagem das trotinetes que existem num raio D
                        // System.out.println(sMR.getMapa()); Trocar isto para dar print do mapa no cliente ( talvez na propria classe cliente )
                        // TaggedConnection.Frame coordx = c.receive();
                        TaggedConnection.Frame coordy = c.receive();
                        List<Tuple> tuplos = sMR.getMapa().checkT_Livres(2, frame.data[0], coordy.data[0]);

                        c.send(frame.tag, new byte[]{(byte) tuplos.size()});

                        for(Tuple t : tuplos){
                            c.send(frame.tag, new byte[]{(byte)t.getX()});
                            c.send(frame.tag, new byte[]{(byte)t.getY()});
                            //t.serialize(out);
                            //out.flush();
                        }
                        //c.send(frame.tag, baos.toByteArray());

                        break;

                    case 4: // Cliente quer ver as Recompensas que existem num raio D
                        TaggedConnection.Frame x = c.receive();
                        TaggedConnection.Frame y = c.receive();
                        for(Recompensa r : this.sMR.getSistemaDeRecompensas().getRecompensasDistancia(new Tuple(x.data[0],y.data[0]), 2)){ // Isto são todas as recompensas, não é suposto
                            r.serialize(out);
                            out.flush();
                        }
                        c.send(tag,baos.toByteArray());

                        break;

                    case 5: // Cliente quer reservar uma trotinete (Vai dar coordenadas de onde quer estacionar)
                        System.out.println(sMR.getMapa());
                        c.receive();

                    case 6:
                        break;

                }
            }
        } catch (Exception ignored) { } // criar uma (?)
        // Fechar Socket?
    }
}


public class Server{
    public static void main(String[] args) throws Exception {
        ServerSocket socket = new ServerSocket(12345);
        sistemaMapaRecompensas sMR = new sistemaMapaRecompensas();
        Login login = new Login();

        // Criar thread de recompensas forever????
        // Criar thread de notificações forever????
        while(true){
            Socket s = socket.accept();
            TaggedConnection c = new TaggedConnection(s);
            Thread t = new Thread(new ServerWorker(sMR, s,c,login));
            t.start();
        }
    }
}



