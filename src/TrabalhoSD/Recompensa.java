package TrabalhoSD;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Recompensa {
    private Tuple origem;
    private Tuple destino;
    private double distancia;
    private double ganho;


    public Recompensa(){
        this.origem = null;
        this.destino = null;
        this.distancia = 0;
        this.ganho = 0;
    }
    public Recompensa(Tuple origem, Tuple destino){
        this.origem = origem;
        this.destino = destino;
        this.distancia = Math.abs((origem.getX())- origem.getY())+Math.abs(destino.getX()-destino.getY());
        this.ganho = this.distancia *0.5;
    }

    public Recompensa(Tuple origem, Tuple destino, double distancia, double ganho){
        this.origem = origem;
        this.destino = destino;
        this.distancia = distancia;
        this.ganho = ganho;
    }

    public Tuple getOrigem(){
        return this.origem;
    }

    public Tuple getDestino() { return this.destino; }

    public double getDistancia() { return distancia;}

    public double getGanho() { return ganho;}

    @Override
    public String toString() {
        return this.origem + " -> " + this.destino + "- Ganho: " + this.ganho;
    }

    public void serialize(DataOutputStream out) throws IOException {
        this.origem.serialize(out);
        this.destino.serialize(out);
        out.writeDouble(this.distancia);
        out.writeDouble(this.ganho);
    }

    public Recompensa deserialize(DataInputStream in) throws IOException {
        Tuple origem = Tuple.deserialize(in);
        Tuple destino = Tuple.deserialize(in);
        double distancia = in.readDouble();
        double ganho = in.readDouble();

        return new Recompensa(origem,destino,distancia,ganho);
    }
}

