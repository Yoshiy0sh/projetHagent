

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class AgentCount implements Agent {
    private static final long serialVersionUID = 1L;

    private String name;
    private Node origin;
    private final Node destination1 = new Node("localhost",8082);
    private final Node destination2 = new Node("localhost",8083);
    private Node current;
    private boolean start = true;
    private Hashtable<String, Object> data = new Hashtable<>();
    private transient Hashtable<String, Object> dataCurrentServer;

    transient private byte[] agentClassBytes;

    @Override
    public void setAgentClassBytes(byte[] classByte){
        agentClassBytes = classByte;
    }

    @Override
    public void main() {
        if (start) {
            start = false;
            System.out.println("je suis reveillé c'est parti!, je vais vers " + destination1.toString());
            current = destination1;
            try {
                move(destination1);
            } catch (MoveException e) {
                System.out.println("j'ai pas pu partir");
            }
        } else if (current.equals(destination1)) {
            // traitement sur dataCurrentServer
            System.out.println("on est bien arrivés à " + destination1.toString());

            // traitement
            int[] intArray = (int[]) dataCurrentServer.get("intArray");
            data.put("result1", sum(intArray));
            
            current = destination2;
            try {
                move(destination2);
            } catch (MoveException e) {
                System.out.println("on a pas pu aller à la destination2");
            }
        } else if (current.equals(destination2)) {
            System.out.println("on est bien arrivés à " + destination2.toString());

            // traitement
            int[] intArray = (int[]) dataCurrentServer.get("intArray");
            data.put("result2", sum(intArray));

            try {
                System.out.println("je retourne vers origin");
                System.out.println("mes données : " + data);
                back();
            } catch (MoveException e) {
                System.out.println("on a pas pu revenir on est bloqués");
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
    public void init(String name, Node origin, byte[] agentClassBytes) {
        this.name = name;
        this.origin = origin;
        this.agentClassBytes = agentClassBytes;

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
            DataOutputStream out = new DataOutputStream(os);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            byte[] objectBytes = baos.toByteArray();

            

            out.writeUTF("AgentCount");
            out.writeInt(agentClassBytes.length);
            System.out.println("la taille du .class" + agentClassBytes.length);
            out.write(agentClassBytes);
            out.flush();

            out.writeInt(objectBytes.length);
            out.write(objectBytes);
            
            out.flush();
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