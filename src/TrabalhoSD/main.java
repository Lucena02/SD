package TrabalhoSD;

import java.util.List;

public class main {

    public static void main(String args[]) {
        /*
        TrabalhoSD.Login l = new TrabalhoSD.Login();
        l.contas.put("Ricardo", "12345");
        l.contas.put("Xavier", "vrumvrum");
        if (l.tentativaLogin("Ricardo", "12345")) System.out.println("Sucesso");
        Cliente c = new Cliente("Ricardo", "12345");
        */

        Mapa m = new Mapa();
        m.printMatrix();
        //m.checkT_Livres(2, 2,2);
        SistemaRecompensas sistema = new SistemaRecompensas(m.getLock());
        //sistema.update_Recompensas(m);
        System.out.print("\n \n \n");
        System.out.print(sistema.getRecompensasDistancia(new Tuple(2,2), 5));
        System.out.print("\n \n \n");
        System.out.print(sistema.getRecompensasDistancia(new Tuple(3,3), 7));
        System.out.print("\n \n \n");
        System.out.print(sistema.getRecompensasDistancia(new Tuple(4,4), 6));
        System.out.print("\n \n \n");
        System.out.print(sistema.getRecompensasDistancia(new Tuple(5,5), 8));
        System.out.print("\n \n \n");
        System.out.print(sistema.getRecompensasDistancia(new Tuple(6,6), 10));


    }

}
