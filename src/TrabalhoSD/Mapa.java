package TrabalhoSD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa {
    public final static int N=20;
    private int [][] map;
    private final static int MaxT=50;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Mapa(){
        this.map = new int[N][N];

        for (int i = 0; i < N; i++) {            // Cada posição começa por ter 0 trotinetes livres
            for (int j = 0; j < N; j++) {
                map[i][j] = 0;
            }
        }

        Random rand = new Random();
        for(int k = 0; k < MaxT; k++){           // Povoamento com trotinetes
            int randX = rand.nextInt(20);
            int randY = rand.nextInt(20);
            map[randX][randY] += 1;
        }
    }

    public ReentrantReadWriteLock getLock(){
        return this.lock;
    }

    public void updateMap(int x, int y, int value){
        try { lock.writeLock().lock();
            this.map[x][y] = value;
        }finally {
            this.lock.writeLock().unlock();
        }
    }


    public int getRows(){
        return map.length;
    }

    public int getColumns() {
        return map[0].length;
    }

    public int getNTrotinetes(int row, int column) {
        try { lock.readLock().lock();
            return map[row][column];
        }finally {
            this.lock.readLock().unlock();
        }
    }

    public void addTrotinete(int row, int column) {
        try { lock.writeLock().lock();
            this.map[row][column] += 1;
        }finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeTrotinete(int x, int y){
        try { lock.writeLock().lock();
            this.map[x][y] -= 1;
        }finally {
            this.lock.writeLock().unlock();
        }
    }

    public void printMatrix() {
        try { lock.readLock().lock();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    System.out.print(this.map[i][j] + " ");
                }
                System.out.println();
            }
        }finally {
            this.lock.readLock().unlock();
        }
    }

    public List<Tuple> checkT_Livres(int fDistance, int coordX, int coordY){ // Dada uma localização e uma distância, diz onde estão as trotinetes dado um determinado raio
        List<Tuple> Tuplos = new ArrayList<>();
         // Verificar isto
        try {
            this.lock.readLock().lock();
            for (int i = coordX - fDistance; i <= coordX + fDistance; i++) {
                for (int j = coordY - fDistance; j <= coordY + fDistance; j++) {
                    if (i >= 0 && i < N && j >= 0 && j < N && this.map[i][j] != 0 && (Math.abs((coordX)-i)+Math.abs(coordY-j)) <= fDistance) { //pq !(i == coordX && j == coordY) ?
                        Tuplos.add(new Tuple(i,j));
                    }
                }
            }
            return Tuplos;

        }
        finally{this.lock.readLock().unlock();}
    }


    public Tuple find_Perto(int fDistance, int coordX, int coordY){ //TESTAR
        Tuple closestTuple = new Tuple();
        int minDistance = Integer.MAX_VALUE;
        try {
            this.lock.readLock().lock();

            for (int i = coordX - fDistance; i <= coordX + fDistance; i++) {
                for (int j = coordY - fDistance; j <= coordY + fDistance; j++) {
                    int distance = Math.abs((coordX)-i)+Math.abs(coordY-j);
                    if (i >= 0 && i < N && j >= 0 && j < N  && this.map[i][j] != 0 && distance <= fDistance) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestTuple.setX(i);
                            closestTuple.setY(j);
                        }
                    }
                }
            }
            return closestTuple;
        }
        finally{this.lock.readLock().unlock();}
    }

    public List<Tuple> checkT_Abundantes(){ // Coloca numa lista as coordenadas onde há mais do que uma trotinete
        List<Tuple> Tuplos = new ArrayList<>();

        try{
            this.lock.readLock().lock();
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    if(this.map[i][j] > 1) Tuplos.add(new Tuple(i,j));
                }
            }
            return Tuplos;
        } finally{this.lock.readLock().unlock();}
    }

    public List<Tuple> checkT_Empty(int fDistance){ //Coloca numa lista as coordenadas onde não há trotinetes
        List<Tuple> Tuplos = new ArrayList<>();

        try{
            this.lock.readLock().lock();
            for(int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    if (checkT_Livres(fDistance, i, j).isEmpty()){
                        Tuplos.add(new Tuple(i,j));
                    }
                }
            }
            return Tuplos;
        } finally{this.lock.readLock().lock();}
    }















}
