package TrabalhoSD;

public class Recompensa {
    private Tuple origem;
    private Tuple destino;
    private double distancia;
    private double ganho;

    public Recompensa(Tuple origem, Tuple destino){
        this.origem = origem;
        this.destino = destino;
        this.distancia = Math.abs((origem.getX())- origem.getY())+Math.abs(destino.getX()-destino.getY());
        this.ganho = this.distancia *0.5;
    }

    public Tuple getOrigem(){
        return this.origem;
    }


    @Override
    public String toString() {
        return this.origem + " -> " + this.destino + "- Ganho: " + this.ganho;
    }
}

