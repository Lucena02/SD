package TrabalhoSD;

import java.util.Scanner;

public class Menu {

    public Menu() {}

    //apresenta o menu inicial ao cliente
    public static void apresentarMenuGF(){
        System.out.println("--------Menu GF--------");
        System.out.println("0. Sair");
        System.out.println("1. Register");
        System.out.println("2. Login");
    }

    //apresenta o menu pos login sucedido
    public static void apresentarMenuLog(){
        System.out.println("--------Menu Login--------");
        System.out.println("0. Sair");
        System.out.println("1. Procurar trotinetes livres");
        System.out.println("2. Procurar recompensas");
        System.out.println("3. Reservar trotinete");
        System.out.println("4. Estacionar trotinete");
        System.out.println("5. Ativar/Desativar Notificacoes");
    }



    //le a resposta do cliente ao menu
    public static int readOption() {
        int op;
        Scanner is = new Scanner(System.in);
        System.out.print("Opcao: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        return op;
    }




    //da print à string "texto" e devolve a resposta do cliente (string)
    public static String lerString(String texto) {
        Scanner is = new Scanner(System.in);
        System.out.print(texto);
        return is.nextLine();
    }

    //da print à string "texto" e devolve a resposta do cliente (int)
    public static int lerInt(String text) {
        int op;
        Scanner is = new Scanner(System.in);
        System.out.print(text);
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        }
        catch (NumberFormatException e) { // Não foi inscrito um int
            op = -1;
        }
        return op;
    }
}
