package TrabalhoSD;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {

    private String username;
    private String password;
    private boolean login;
    private boolean hasReserva; // se já tem uma reserva ou não
    private boolean notificacao; // se quer receber notificações

    public Cliente(){
        this.username= "";
        this.password ="";
        this.login = false;
        this.hasReserva = false;
        this.notificacao=false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isHasReserva() {
        return hasReserva;
    }

    public void setHasReserva(boolean hasReveserva) {
        this.hasReserva = hasReveserva;
    }

    public boolean isNotificacao() {
        return notificacao;
    }

    public void setNotificacao(boolean notificacao) {
        this.notificacao = notificacao;
    }


    public static void comunicarLocalizacao(Demultiplexer m, Menu menu, int tag){
        //System.out.println("Nao implementado");
        int x = menu.lerInt("Insira a coordenada x: ");
        int y = menu.lerInt("Insira a coordenada y: ");

        boolean b;

        try{
            // send request
            m.send(tag, new byte[]{(byte) x});
            m.send(tag, new byte[]{(byte) y});
            Thread.sleep(100);

            //System.out.println("Comunicacao bem sucedida");


        }catch(Exception e){
            System.out.println("Erro: "+e);
        }
    }




    public void register(Demultiplexer m, Menu menu){

        String user=menu.lerString("Escolha o username: ");
        String pass=menu.lerString("Escolha a password: ");
        boolean b;      // para que isto ???

        try{
            m.send(1, user.getBytes());
            m.send(1, pass.getBytes());
            Thread.sleep(100);

            byte[] data = m.receive(1);

            if (data[0] == 1) System.out.println("Sucesso a criar conta");
            else System.out.println("Erro ao criar conta");

            setUsername(user);
            setPassword(pass);

        }catch(Exception e){
            System.out.println("Erro: "+e);
        }
    }


    public void login(Demultiplexer m, Menu menu){

        String user=menu.lerString("Insira o username: ");
        String pass=menu.lerString("Insira a password: ");
        boolean b;

        try{
            // send request
            //b = stub.login(user,pass);
            //comunicar com o server!!!!!!!

            m.send(2, user.getBytes()); // codigo 1 para login
            m.send(2, pass.getBytes());
            Thread.sleep(100);

            byte[] data = m.receive(2);

            if (data[0] == 1) {
                System.out.println("Sucesso a efetuar o login da conta");
                setLogin(true);
            }
            else System.out.println("Erro a efetuar o login da conta");



        }catch(Exception e){

            System.out.println(e);

        }
    }
}
