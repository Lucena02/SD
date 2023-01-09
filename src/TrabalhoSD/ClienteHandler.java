package TrabalhoSD;

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

                                    m.receive(4);


                                    //falta dar parse aos dados recebidos
                                    //falta enviar a resposta





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

                                    m.receive(5);

                                    //falta interpretar a resposta do sevidor
                                    //sucesso -> coordenadas da trota e código de reserva
                                    //insucesso -> codigo de insucesso


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