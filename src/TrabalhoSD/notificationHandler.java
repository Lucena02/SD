package TrabalhoSD;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class notificationHandler {

    private Map<String, Tuple>  tuplosClientes = new HashMap<>();
    private Map<String, TaggedConnection> clientes = new HashMap<>();
    private int fdistante=2;
    private final ReentrantLock l = new ReentrantLock();
    private Condition c = l.newCondition();


    public notificationHandler(){}

    /**
     * Método que adiciona uma Socket a um utilizador.
     *
     * @param username String com o nome do utilizador.
     * @param c    Taggedconnection do utilizador.
     */

    public void addClient(String username, TaggedConnection c, Tuple t ) {
        try {
            l.lock();
            this.clientes.put(username, c);
            this.tuplosClientes.put(username, t);
        } finally {
            l.unlock();
        }
    }

    /**
     * Método que remove uma Socket de um utilizador.
     *
     * @param username String com o nome do utilizador.
     */

    public void removeClient(String username) {
        try {
            l.lock();
            this.clientes.remove(username);
            this.tuplosClientes.remove(username);
        } finally {
            l.unlock();
        }
    }

    public void signalSistema(){
        this.c.signal();
    }


    public void notificarALL(List<Recompensa> recompensasList){


        List<Recompensa> recompensasEnviadas = new ArrayList<>();
        try{

            l.lock();

            this.c.await();

            for(Map.Entry<String, Tuple> entry: this.tuplosClientes.entrySet() ){
                for(Recompensa r : recompensasList){
                    if(r.getOrigem().calculaDistancia(entry.getValue()) <= this.fdistante){
                        recompensasEnviadas.add(r);
                    }
                }
                TaggedConnection tcliente = this.clientes.get(entry.getKey());
                tcliente.send(10, new byte[]{(byte)recompensasEnviadas.size()});

                for(Recompensa r : recompensasEnviadas) {
                    tcliente.send(10, new byte[]{(byte)r.getOrigem().getX()});
                    tcliente.send(10, new byte[]{(byte)r.getOrigem().getY()});
                    tcliente.send(10, new byte[]{(byte)r.getDestino().getX()});
                    tcliente.send(10, new byte[]{(byte)r.getDestino().getY()});
                    tcliente.send(10, new byte[]{(byte)r.getDistancia()});
                    tcliente.send(10, new byte[]{(byte)r.getGanho()});
                }
                recompensasEnviadas.clear();
            }

            } catch (IOException e) {
            throw new RuntimeException(e);
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
                l.unlock();
            }
    }


}
