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
        SistemaRecompensas sistema = new SistemaRecompensas();
        sistema.update_Recompensas(m);
        System.out.print("\n \n \n");
        System.out.print(sistema.getMapRecompensas().values());
        System.out.print("\n \n \n");
        //System.out.print("oi");
        System.out.print("CheckaRecompensas agora:\n");
        List<Integer> l = sistema.checkRecompensas(new Tuple(4,4), 2);
        System.out.print(l);
        List<Integer> ll = sistema.checkRecompensas(new Tuple(10,10), 2);
        System.out.print(ll);


    }

}
