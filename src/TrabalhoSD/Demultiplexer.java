package TrabalhoSD;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Deque;

public class Demultiplexer implements AutoCloseable {
    private TaggedConnection tc;
    private Map<Integer, TaggedFrame> queueMsg;
    private ReentrantLock l;
    public Demultiplexer(TaggedConnection conn) {
        this.tc = conn;
        this.queueMsg = new HashMap<>();
        this.l = new ReentrantLock();
    }
    public void start() throws IOException {
        // Tenho que acrescentar uma thread sozinha a executar este código
        while(true){
            TaggedConnection.Frame f = this.tc.receive();
            this.l.lock();
            try{
                if(!this.queueMsg.containsKey(f.tag)){
                    TaggedFrame tf = new TaggedFrame(this.l);
                    this.queueMsg.put(f.tag, tf);
                }
                this.queueMsg.get(f.tag).msgDeque.add(f);
                this.queueMsg.get(f.tag).c.signal();
            }finally {
                this.l.unlock();
            }
        }
    }

    public void send(TaggedConnection.Frame frame) throws IOException {
        this.tc.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        this.tc.send(tag, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        this.l.lock();
        try{
            while(this.queueMsg.get(tag).msgDeque.isEmpty())
                this.queueMsg.get(tag).c.await();

            TaggedConnection.Frame f = this.queueMsg.get(tag).msgDeque.element();
            return f.data;
        }finally {
            this.l.unlock();
        }
    }

    public void close() throws IOException {
        this.tc.close();
    }
}

class TaggedFrame{
    public Condition c;
    public Deque<TaggedConnection.Frame> msgDeque;

    public TaggedFrame(ReentrantLock l){
        this.c = l.newCondition();
        this.msgDeque = new ArrayDeque<>();
    }
}
