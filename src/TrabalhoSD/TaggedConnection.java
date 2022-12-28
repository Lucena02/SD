package TrabalhoSD;
import java.io.*;
import java.net.Socket;

// Isto serve para dar uma "tag" à conexão. Serve para distinguir entre diferentes ligações para que o servidor saiba o que
// fazer dependendo das diferentes tags da conexão. Diferentes ações que o servidor deve tomar.
public class TaggedConnection implements AutoCloseable {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag; this.data = data;
        }
    }
    public TaggedConnection(Socket socket) throws IOException {
        this.s = socket;

        InputStream in = this.s.getInputStream();
        this.in = new DataInputStream(in);

        OutputStream out = this.s.getOutputStream();
        this.out = new DataOutputStream(out);
    }
    public void send(Frame frame) throws IOException {
        this.out.writeInt(frame.tag);
        this.out.writeInt(frame.data.length);
        this.out.write(frame.data);
        this.out.flush();
    }

    public void send(int tag, byte[] data) throws IOException {
        this.out.writeInt(tag);
        this.out.writeInt(data.length);
        this.out.write(data);
        this.out.flush();
    }

    public Frame receive() throws IOException {
        int tag = this.in.readInt();
        int comp = this.in.readInt();
        byte [] data = new byte[comp];
        this.in.readFully(data);
        return new Frame(tag, data);
    }

    public void close() throws IOException {
        this.s.close();
    }
}
