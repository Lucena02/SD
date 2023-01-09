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

        byte[] buffer = new byte[256];
        ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
        DataInputStream in = new DataInputStream(baos);

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
            System.out.println(); //?????

            switch (op) {
                case 0:
                    System.out.println("A sair...");
                    break;
                case 1:
                    Thread register = new Thread(() -> {
                        System.out.println("Register:");
                        cliente.register(m, menu);
                    });
                    register.start();
                    register.join();
                    break;
                case 2:
                    Thread login = new Thread(() -> {
                        System.out.println("Login:");
                        cliente.login(m, menu);
                    });
                    login.start();
                    login.join();
                    break;
                default:
                    System.out.println("Erro na escolha");
                    break;
            }

            System.out.println(); //??????
        }


        if (cliente.isLogin()) {

            while (op != 0) {
                try {
                    menu.apresentarMenuLog();
                    op = menu.readOption();
                    System.out.println();

                    switch (op) {
                        case 0:
                            System.out.println("A sair...");
                            s.close();
                            break;
                        case 1: // Listar trotinetes livres
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


                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            trotiLivre.start();
                            trotiLivre.join();
                            break;
                        case 2:
                            System.out.println("Recompensas Disponiveis:");

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

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            recompensas.start();
                            recompensas.join();
                            break;
                        case 3:
                            System.out.println("Reservar trotinete:");

                            Thread reserva = new Thread(() -> {
                                try {
                                    cliente.comunicarLocalizacao(m, menu, 5);

                                    byte[] size = m.receive(5);
                                    int tamanho = size[0];

                                    switch (tamanho){

                                        case 1:

                                            //erro na escolha da trotinete
                                            byte [] resposta1 = m.receive(5);
                                            String erro = new String(resposta1);

                                            System.out.println(erro);
                                            break;

                                        case 2:

                                            //sucesso na reserva da trotinete
                                            byte [] datax = m.receive(5);
                                            int coordx = datax[0];
                                            byte [] datay = m.receive(5);
                                            int coordy = datay[0];
                                            byte [] resposta2 = m.receive(5);
                                            String sucesso = new String(resposta2);

                                            System.out.println("Trotinete reservada com sucesso!\n\nCoordenadas: x->" + coordx + " y->" + coordy + "\nCodigo de reserva: " + sucesso);

                                        default:

                                            System.out.println("Erro na escolha");
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
                            System.out.println("Estacionar trotinete");

                            Thread estaciona = new Thread(() -> {
                                try {
                                    cliente.comunicarLocalizacao(m, menu, 6);

                                    byte[] teste = menu.lerString("Insira o codigo de reserva").getBytes();
                                    m.send(6, teste);

                                    Thread.sleep(100);
                                    m.receive(6);

                                    //falta interpretar a resposta do sevidor
                                    //sucesso -> coordenadas da trota e código de reserva
                                    //insucesso -> codigo de insucesso


                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            estaciona.start();
                            estaciona.join();
                            break;
                        case 5:
                            System.out.println("Notificoes");

                            //

                            break;
                        default:
                            System.out.println("Erro na escolha");
                            break;
                    }

                } catch (Exception e) {

                    s.close();
                }
            }
        }
    }
}