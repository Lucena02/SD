package TrabalhoSD;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Login {
    public Map<String, String> contas;
    private ReentrantLock lock;


    public Login() {
        this.contas = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public boolean tentativaLogin(String username, String password){
        try {
            this.lock.lock();
            if (!contas.containsKey(username)) {               // Se a conta não existir
                return false;
            }
            else {
                if (!Objects.equals(password, contas.get(username))) {
                    return false;
                }
            }
        } finally {this.lock.unlock();}

        return true;
    }

    public boolean register(String username, String password){
        try{
            this.lock.lock();
            if(contas.containsKey(username)) return false;
            else {
                contas.put(username,password);
                return true;
            }
        } finally{this.lock.unlock();}
    }

}
