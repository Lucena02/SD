package TrabalhoSD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClienteHandler {


    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost", 12345);
        Demultiplexer m = new Demultiplexer(new TaggedConnection(s));
        Cliente cliente = new Cliente();


        Menu menu = new Menu();

        // menu login ou register
        int op = -1;

        Thread thread =
                new Thread(() -> {
                    try {
                        m.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        thread.start();


        while (op != 0 && !(cliente.isLogin())) {
            menu.apresentarMenuGF();
            op = menu.readOption();
            System.out.println();

            switch (op) {
                case 0:
                    System.out.println("A sair...\n\n");
                    break;
                case 1:
                    Thread register = new Thread(() -> {
                        System.out.println("Register:\n\n");
                        cliente.register(m, menu);
                    });
                    register.start();
                    register.join();
                    break;
                case 2:
                    Thread login = new Thread(() -> {
                        System.out.println("Login:\n\n");
                        cliente.login(m, menu);
                    });
                    login.start();
                    login.join();
                    break;
                default:
                    System.out.println("Erro na escolha\n\n");
                    break;
            }

            System.out.println();
        }


        if (cliente.isLogin()) {

            while (op != 0) {
                try {
                    menu.apresentarMenuLog();
                    op = menu.readOption();
                    System.out.println();

                    switch (op) {
                        case 0:
                            System.out.println("A sair...\n\n");
                            s.close();
                            break;

                        case 1: // Listar trotinetes livres
                            System.out.println("Trotinetes Disponiveis:");
                            Thread trotiLivre = new Thread(() -> {
                                try {
                                    cliente.comunicarLocalizacao(m, menu, 3);

                                    byte[] size = m.receive(3);
                                    List<Tuple> result = new ArrayList<>();

                                    for(int i = 0; i < size[0]; i++){
                                        byte [] datax = m.receive(3);
                                        int coordx = datax[0];
                                        byte [] datay = m.receive(3);
                                        int coordy = datay[0];
                                        result.add(new Tuple(coordx,coordy));
                                    }

                                    System.out.println(result);


                                }
                                catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            trotiLivre.start();
                            trotiLivre.join();
                            break;
                        case 2:
                            System.out.println("Recompensas Disponiveis:\n\n");

                            Thread recompensas = new Thread(() -> {
                                try {
                                    cliente.comunicarLocalizacao(m, menu, 4);

                                    byte[] sizeR = m.receive(4);
                                    List<Recompensa> rewards = new ArrayList<>();

                                    for(int i = 0; i < sizeR[0]; i++){
                                        byte [] datax1 = m.receive(4);
                                        byte [] datay1 = m.receive(4);
                                        byte [] datax2 = m.receive(4);
                                        byte [] datay2 = m.receive(4);
                                        byte [] distancia = m.receive(4);
                                        byte [] ganho = m.receive(4);
                                        rewards.add(new Recompensa(new Tuple(datax1[0], datay1[0]), new Tuple(datax2[0], datay2[0]), distancia[0], ganho[0]));
                                    }

                                    System.out.println(rewards);

                                }
                                catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            recompensas.start();
                            recompensas.join();
                            break;
                        case 3:
                            System.out.println("Reservar trotinete:\n\n");

                            Thread reserva = new Thread(() -> {
                                try {
                                    System.out.println("Introduza a sua localizacao");
                                    cliente.comunicarLocalizacao(m, menu, 5);

                                    Thread.sleep(500);

                                    //"tamanho" (numero de mensagens de resposta)
                                    byte[] inteiro = m.receive(5);
                                    int tamanho = inteiro[0];


                                    switch (tamanho){

                                        case 1:
                                            System.out.println("Failure");
                                            break;
                                        case 2:
                                            //sucesso na reserva da trotinete

                                            byte [] resposta2 = m.receive(5);
                                            String sucesso = new String(resposta2);
                                            cliente.setHasReserva(true);

                                            System.out.println("Trotinete reservada com sucesso!\n\n" + sucesso);
                                            cliente.setHasReserva(true);

                                            break;
                                        default:
                                            System.out.println("Não deu\n");
                                            cliente.setHasReserva(false);
                                            break;
                                    }

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            reserva.start();
                            reserva.join();
                            break;
                        case 4:
                            System.out.println("Estacionar trotinete:\n\n");

                            Thread estaciona = new Thread(() -> {
                                try {
                                    if (cliente.isHasReserva()) {

                                        byte[] teste = menu.lerString("Insira o codigo de reserva\n").getBytes();
                                        cliente.comunicarLocalizacao(m, menu, 6);

                                        m.send(6, teste);

                                        Thread.sleep(500);

                                        //valor a pagar
                                        byte[] resposta3 = m.receive(6);
                                        double valorviagem = resposta3[0];

                                        if (valorviagem != -1) {

                                            if(valorviagem < 0) valorviagem = valorviagem * -1;

                                            System.out.println("Tem de pagar " + valorviagem + " pela viagem\n");
                                            cliente.setHasReserva(false);
                                        }
                                        else {
                                            System.out.println("Esse codigo nao esta associado a nenhuma reserva");
                                        }
                                    }
                                    else {
                                        System.out.println("Nao e possivel associar uma trotinete ao utilizador atual");
                                    }
                                } catch (Exception e){
                                    throw new RuntimeException(e);
                                }
                            });
                            estaciona.start();
                            estaciona.join();
                            break;
                        case 5:
                            System.out.println("Notificoes:\n\n");

                            m.send(7, cliente.getUsername().getBytes());
                            cliente.comunicarLocalizacao(m, menu, 7);

                            Thread.sleep(100);

                            //o servidor devolve um bool (true -> confirmacao das alteracoes)
                            byte [] resposta1 = m.receive(7);
                            boolean notificacoes = resposta1[0] != 0;

                            if(notificacoes) {
                                if (!cliente.isNotificacao()) {

                                    System.out.println("Notidicacoes ativadas com  sucesso!\n");
                                    cliente.setNotificacao(true);
                                }
                                else{

                                    System.out.println("Notificacoes desativadas com sucesso!\n");
                                    cliente.setNotificacao(false);
                                }
                            }

                            break;
                        default:
                            System.out.println("Erro na escolha\n");
                            break;
                    }

                } catch (Exception e) {

                    s.close();
                }
            }
        }
        s.close();
    }
}