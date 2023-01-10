package TrabalhoSD;

import com.sun.nio.sctp.NotificationHandler;
import jdk.jfr.consumer.RecordedMethod;

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
    private HashMap<String,Recompensa> clienteRecompensa = new HashMap<>();

    public sistemaMapaRecompensas(){
        this.mapa= new Mapa();
        ReentrantReadWriteLock l = mapa.getLock();
        this.sistemaDeRecompenas = new SistemaRecompensas(l);
    }


    public Mapa getMapa(){
        return this.mapa;
    }

    public SistemaRecompensas getSistemaDeRecompensas() { return this.sistemaDeRecompenas;}

    public HashMap<String,Tuple> getCodigosReserva () {
        return this.codigosReserva;
    }


    public String reservaTrot(Tuple tuplo){

        Tuple local =null;
        Recompensa r = null;



        try {
            lock.lock();

            local = mapa.find_Perto(2, tuplo.getX(), tuplo.getY());
            if (!local.existeTuplo()) return null;
            mapa.removeTrotinete(local.getX(), local.getY());

        }finally {
            lock.unlock();
        }

        String code = generateRandomCode();
        System.out.println(code);

        try {
            lock.lock();
            codigosReserva.put(code, local);
            r = sistemaDeRecompenas.verificaRecompensa(tuplo);
            if(!(r==null)) this.clienteRecompensa.put(code,r);

        }finally {
            lock.unlock();
        }


        String resposta = "Codigo de Reserva: " +
                code +
                "\n" +
                local;

        long startTime = System.currentTimeMillis();



        try {
            lock.lock();
            startTimeMap.put(code,startTime);
        }finally {
            lock.unlock();
        }


        this.sistemaDeRecompenas.signalSistema();


        return resposta;
    }

    public Double estacionaTrot(String code, Tuple localFinal){ //TESTAR
        long endTime = System.currentTimeMillis();
        Tuple localInicial;
        long startTime;
        try {
            lock.lock();


            localInicial = codigosReserva.get(code);
            startTime = startTimeMap.get(code);
            double custo = calculaViagem(startTime, endTime, localInicial, localFinal);



            mapa.addTrotinete(localFinal.getX(), localFinal.getY());
            /*
            if(this.clienteRecompensa.containsKey(code)) {
                Recompensa r = this.clienteRecompensa.get(code);
                this.clienteRecompensa.remove(code);

                Tuple tt = r.getDestino();
                if(localFinal.equals(tt)){
                    custo = custo - r.getGanho();
                }
            }
            this.sistemaDeRecompenas.signalSistema();
            */
            return custo;
        } finally{lock.unlock();}


    }



    public double calculaViagem(Long inicioTime,Long finalTime,Tuple localInicial, Tuple localFinal){ //meti uma função para calcular o custo atoa. TESTAR
        double custo =0;

        double distanciaViagem = localFinal.calculaDistancia(localInicial);
        long elapsedTime = finalTime - inicioTime;

        custo = distanciaViagem * elapsedTime*0.01;

        return custo;
    }

    public static String generateRandomCode() {
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
                System.out.println("-------------------------------------------");

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
                        //TaggedConnection.Frame x = c.receive();
                        TaggedConnection.Frame y = c.receive();

                        List<Recompensa> rewards = this.sMR.getSistemaDeRecompensas().getRecompensasDistancia(new Tuple(frame.data[0],y.data[0]), 2);
                        c.send(tag, new byte[]{(byte)rewards.size()});


                        for(Recompensa r : rewards) {
                            c.send(frame.tag, new byte[]{(byte)r.getOrigem().getX()});
                            c.send(frame.tag, new byte[]{(byte)r.getOrigem().getY()});
                            c.send(frame.tag, new byte[]{(byte)r.getDestino().getX()});
                            c.send(frame.tag, new byte[]{(byte)r.getDestino().getY()});
                            c.send(frame.tag, new byte[]{(byte)r.getDistancia()});
                            c.send(frame.tag, new byte[]{(byte)r.getGanho()});
                        }
                        break;

                    case 5: // Cliente quer reservar uma trotinete (Vai dar coordenadas de onde quer reservar)
                        TaggedConnection.Frame yy = c.receive();

                        String codigo = this.sMR.reservaTrot(new Tuple(frame.data[0], yy.data[0]));

                        //System.out.println(codigo);

                        if (codigo == null) {
                            c.send(tag,new byte[]{(byte)1});
                        }
                        else {
                            c.send(tag,new byte[]{(byte)2});
                            System.out.println(codigo);
                            c.send(tag,codigo.getBytes());
                        }

                        break;


                    case 6: //cliente quer estacionar a trotinete (vai dar coordenadas onde quer estacionar  -> pode receber recompensa)

                        TaggedConnection.Frame yyy = c.receive();

                        TaggedConnection.Frame infoCode = c.receive();
                        byte [] resposta = infoCode.data;
                        String code = new String(resposta);
                        System.out.println("dede");

                        HashMap<String,Tuple> codigosCheck = sMR.getCodigosReserva();

                        if (codigosCheck.containsKey(code)) {
                            double custo = this.sMR.estacionaTrot(code, (new Tuple(frame.data[0], yyy.data[0])));
                            System.out.println("123123123");
                            c.send(6, new byte[]{(byte) custo});
                        }
                        else {
                            System.out.println("Código errado");
                            c.send(6,new byte[]{(byte) -1});
                        }

                        break;
                    case 7: //cliente quer alterar o seu estado de notificacoes





                        break;
                    default:
                        //erro na tag
                        System.out.println("Erro na escolha da tag");
                        break;
                }
            }
        } catch (Exception ignored) { }
    }
}


public class Server{
    public static void main(String[] args) throws Exception {
        ServerSocket socket = new ServerSocket(12345);
        sistemaMapaRecompensas sMR = new sistemaMapaRecompensas();
        Login login = new Login();
        notificationHandler nh = new notificationHandler();


        // Thread a executar Notificações
        Thread permaRewards = new Thread(() -> {
            try {
                while(true){
                Thread.sleep(300);
                sMR.getSistemaDeRecompensas().update_Recompensas(sMR.getMapa(),nh);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        permaRewards.start();


        /*
        Thread notificacoes = new Thread(() -> {
            try {
                while(true){
                Thread.sleep(300); // Desnecessário talvez?
                sMR.getSistemaDeRecompensas().update_Recompensas(sMR.getMapa());
                //sMR.getSistemaDeRecompensas().awaitSistema();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        permaRewards.start();
         */

        while(true){
            Socket s = socket.accept();
            TaggedConnection c = new TaggedConnection(s);
            Thread t = new Thread(new ServerWorker(sMR, s,c,login));
            t.start();
        }
    }
}



