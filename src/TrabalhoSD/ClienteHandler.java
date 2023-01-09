package TrabalhoSD;

import java.io.IOException;
import java.net.Socket;

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

            //fazer cenas
            //
            //      falta a cena fixe
            //
            //
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
                        case 1:
                            System.out.println("Introduza as suas coordenadas! \n");
                            System.out.println("");
                            //comunicarLocalizacao();
                            //bar.await(0);
                            break;
                        case 2:
                            System.out.println("Verificar ocupação de uma Localizacao");
                            //verificarOcupacao();
                            //bar.await(0);
                            break;
                        case 3:
                            System.out.println("Imprimir Mapa de ocupaçoes e doentes");
                            //imprimirMapa();
                            //bar.await(0);

                            break;
                        case 4:
                            System.out.println("Comunicar infeção");
                            //comunicarInfecao();
                            //bar.await(0);
                            break;
                        case 5:
                            System.out.println("Notificar quando local está vazio");
                            //notificarLocalVazio();
                            //bar.await(0);
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