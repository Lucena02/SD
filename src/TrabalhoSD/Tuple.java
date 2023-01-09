package TrabalhoSD;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Tuple {
    private int x;
    private int y;

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public  Tuple(){
        this.x =-10;
        this.y=-10;
    }

    public double calculaDistancia(Tuple tuple){
        return Math.abs((x)- tuple.getX())+Math.abs(y-tuple.getY());
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public boolean existeTuplo(){
        if((this.getX() + this.getY())<0){
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return x == tuple.x && y == tuple.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString(){
        return "Localizacao: (" + x + "," + y + ")";
    }


    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
    }

    public static Tuple deserialize(DataInputStream in) throws IOException {
        int x = in.readInt();
        int y = in.readInt();

        return new Tuple(x,y);
    }

}
