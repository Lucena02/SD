package TrabalhoSD;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Server {
    private Mapa mapa;
    private SistemaRecompensas sistemaDeRecompenas;
    private HashMap<String, Tuple> codigosReserva = new HashMap<>();
    private HashMap<String,Long> startTimeMap = new HashMap<>();

    public String reservaTrot(Tuple tuplo){ //TESTAR. O Cliente precisa de dar parse à String para saber o codigo de reserva/failure e a localização.
                                            // Alterar o stringbuilder para facilitar o parse se necessário.

        Tuple local = mapa.find_Perto(3,tuplo.getX(),tuplo.getY());
        if(!local.existeTuplo()) return "FAILURE";

        mapa.removeTrotinete(local.getX(),local.getY());
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

        return resposta.toString();
    }

    public Double estacionaTrot(String code, Tuple localFinal){ //TESTAR
        long endTime = System.currentTimeMillis();

        if(!codigosReserva.containsKey(code)) return -1.0;

        Tuple localInicial = codigosReserva.get(code);
        Long startTime = startTimeMap.get(code);

        double custo = calculaViagem(startTime, endTime, localInicial, localFinal);
        //verificar recompensa


        // no minimo um look aqui
        mapa.addTrotinete(localFinal.getX(),localFinal.getY());
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



