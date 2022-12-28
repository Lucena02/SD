package TrabalhoSD;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;




public class SistemaRecompensas {
    private Map<Integer,Recompensa> mapRecompensas = new HashMap<>();
    private ReentrantReadWriteLock l;
    private Condition c;

    public void SistemaRecompensas(ReentrantReadWriteLock lock){
        this.l= lock;
        this.c = l.writeLock().newCondition();
    }



    public void update_Recompensas(Mapa mapa){ // O sistema sempre que acorda vê o número de trotinetes repetidas e cria recompensas para tais
        if (this.mapRecompensas != null) this.mapRecompensas.clear();

        List <Tuple> trotinetesPremiadas = mapa.checkT_Abundantes();
        List <Tuple> chegadasPremiadas = mapa.checkT_Empty(2);

        Iterator<Tuple> iterator1 = trotinetesPremiadas.iterator();
        Iterator<Tuple> iterator2 = chegadasPremiadas.iterator();

        Tuple TupleTrots;
        Tuple TuploChegada;
        int i=0;

        while (iterator1.hasNext() && iterator2.hasNext()) {
            TupleTrots = iterator1.next();
            TuploChegada = iterator2.next();
            mapRecompensas.put(i, new Recompensa(TupleTrots,TuploChegada));
            i++;
        }

        System.out.print(trotinetesPremiadas);
        System.out.print("\n");
        System.out.print(chegadasPremiadas);
    }

    public List<Integer> checkRecompensas(Tuple tuplo,int fDistance){
        List<Integer> ids = new ArrayList<>();


        for(Map.Entry<Integer, Recompensa> entry : mapRecompensas.entrySet()){
            if(fDistance >= (tuplo.calculaDistancia(entry.getValue().getOrigem()))) {
                ids.add(entry.getKey());
            }
        }
        return ids;
    }


    public Map<Integer, Recompensa> getMapRecompensas() {
        return mapRecompensas;
    }




}