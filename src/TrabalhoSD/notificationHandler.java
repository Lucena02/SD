package TrabalhoSD;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
/*
public class notificationHandler {

    private Map<String, Tuple>  tuplosClientes = new HashMap<>();
    private Map<String, TaggedConnection> clientes = new HashMap<>();
    private int fdistante;

    private final ReentrantLock l = new ReentrantLock();




    /**
     * Método que adiciona uma Socket a um utilizador.
     *
     * @param username String com o nome do utilizador.
     * @param c    Taggedconnection do utilizador.
     */
/*
    public void addClient(String username, TaggedConnection c, Tuple t ) {
        try {
            l.lock();
            this.clientes.put(username, c);
            this.tuplosClientes.put(username, t);
        } finally {
            l.unlock();
        }
    }
*/
    /**
     * Método que remove uma Socket de um utilizador.
     *
     * @param username String com o nome do utilizador.
     */
    /*
    public void removeClient(String username) {
        try {
            l.lock();
            this.clientes.remove(username);
            this.tuplosClientes.remove(username);
        } finally {
            l.unlock();
        }
    }




    public void notificarALL(List<Recompensa> recompensasList){ // Modificar, versão 0.1
        List<Recompensa> recompensasEnviadas = new ArrayList<>();
        try{
            l.lock();

            for(Map.Entry<String, Tuple> entry: this.tuplosClientes.entrySet() ){
                for(Recompensa r : recompensasList){
                    if(r.getOrigem().calculaDistancia(entry.getValue()) <= this.fdistante){
                        recompensasEnviadas.add(r);
                    }
                }


                this.clientes.get(entry.getKey()).send(1,de);
                recompensasEnviadas.clear();
            }





            }
        }finally {
            l.unlock();
        }
    }


}
*/