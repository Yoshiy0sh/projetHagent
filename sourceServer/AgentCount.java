

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

public class AgentCount implements Agent {
    private static final long serialVersionUID = 1L;

    String name;
    Node origin;
    Node destination = new Node("localhost",8082);
    Node current;
    boolean start = true;
    Hashtable<String, Object> data = new Hashtable<>();
    Hashtable<String, Object> dataCurrentServer;

    @Override
    public void main() {
        if (start) {
            start = false;
            System.out.println("je suis reveillé c'est parti!, je vais vers " + destination.toString());
            current = destination;
            try {
                move(destination);
            } catch (MoveException e) {
                System.out.println("j'ai pas pu partir");
                e.printStackTrace();
            }
        } else if (current.equals(destination)) {
            // traitement sur dataCurrentServer
            System.out.println("on est bien arrivés à " + destination.toString());

            // traitement
            int[] intArray = (int[]) dataCurrentServer.get("intArray");
            data.put("result", sum(intArray));

            try {
                back();
            } catch (MoveException e) {
                System.out.println("on a pas pu revenir on est bloqués");
                e.printStackTrace();
            }
        }
    }

    public int sum(int[] t) {
        int sum = 0;
        for (int i = 0; i < t.length; i++) {
            sum += t[i];
        }
        return sum;
    }

    @Override
    public void init(String name, Node origin) {
        this.name = name;
        this.origin = origin;
    }

    @Override
    public void setNameServer(Hashtable<String, Object> ns) {
        dataCurrentServer = ns;
    }

    @Override
    public Hashtable<String, Object> getNameServer() {
        return data;
    }

    //requires Path in Hashtable
    @Override
    public void move(Node target) throws MoveException {
        try {
            Socket s = new Socket(target.getNameServ(), target.getNbPort());

            OutputStream os = s.getOutputStream();

            Path classFile = (Path) dataCurrentServer.get("path");
            dataCurrentServer = null;
            byte[] classBytes = Files.readAllBytes(classFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            byte[] objectBytes = baos.toByteArray();

            DataOutputStream out = new DataOutputStream(os);

            out.writeUTF("AgentCount");
            out.writeInt(classBytes.length);
            out.write(classBytes);

            out.writeInt(objectBytes.length);
            out.write(objectBytes);
            s.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void back() throws MoveException {
        try {
            Socket s = new Socket(origin.getNameServ(), origin.getNbPort());

            OutputStream os = s.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            byte[] objectBytes = baos.toByteArray();

            DataOutputStream out = new DataOutputStream(os);

            out.writeInt(objectBytes.length);
            out.write(objectBytes);
            s.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}