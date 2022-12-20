import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Login {
    public Map<String, String> contas = new HashMap<String, String>();
    private ReentrantLock lock = new ReentrantLock();


    public Login() {}

    public boolean tentativaLogin(String username, String password){
        lock.lock();
        try {
            if (!contas.containsKey(username)) {               // Se a conta não existir
                contas.put(username, password);
                System.out.println("Conta foi registada");
            }
            else {
                if (!Objects.equals(password, contas.get(username))) {
                    System.out.println("Palavra-passe errada");
                    return false;
                }
            }

        }
        finally {lock.unlock();}

        return true;
    }
}
