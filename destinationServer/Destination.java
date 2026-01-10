
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Destination implements Runnable {

    Socket ss;
    Hashtable<String,Object> dataServer;

    public Destination(Socket ss, Hashtable<String,Object> data) {
        this.ss = ss;
        this.dataServer = data;
    }


    @Override
    public void run() {
        try {
            InputStream is = ss.getInputStream();
            DataInputStream in = new DataInputStream(is);
            
            String className = in.readUTF();
            int classLen = in.readInt();
            // System.out.println("on lit " + classLen);
            // byte[] classBytes = in.readNBytes(classLen);
            byte[] classBytes = new byte[classLen];
            in.readFully(classBytes);
            
            int objLen = in.readInt();
            // byte[] objBytes = in.readNBytes(objLen);
            byte[] objBytes = new byte[objLen];
            in.readFully(objBytes);
            
            Loader loader = new Loader();
            loader.addClass(className, classBytes);
            
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(objBytes)) {
                        
                        @Override
                        protected Class<?> resolveClass(ObjectStreamClass desc)
                                throws IOException, ClassNotFoundException {
                            return loader.loadClass(desc.getName());
                        }
                    };
            
            Agent obj = (Agent) ois.readObject();
            try {
                obj.setAgentClassBytes(classBytes);
                obj.setNameServer(dataServer);
                obj.main();
            } catch (MoveException e) {
                e.printStackTrace();
            }
            
            ss.close();
            
        } catch (IOException ex) {
            System.getLogger(Destination.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (ClassNotFoundException ex) {
            System.getLogger(Destination.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Hashtable<String,Object> data = new Hashtable<>();
        int[] intArray = {1,2,3};
        data.put("intArray", intArray);
        try {
            ServerSocket s = new ServerSocket(port);
            while(true) {new Thread(new Destination(s.accept(),data)).start();}
        } catch (IOException ex) {
            System.getLogger(Destination.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
