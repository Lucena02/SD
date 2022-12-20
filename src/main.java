

public class main {

    public static void main(String args[]) {
        Login l = new Login();
        l.contas.put("Ricardo", "12345");
        l.contas.put("Xavier", "vrumvrum");
        if (l.tentativaLogin("Ricardo", "12345")) System.out.println("Sucesso");
        Cliente c = new Cliente("Ricardo", "12345");
    }
}
