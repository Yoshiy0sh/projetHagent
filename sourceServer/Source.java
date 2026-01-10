import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Hashtable;




public class Source implements Runnable{

    private Socket s;
    public Source (Socket s) { this.s = s; }

    @Override
    public void run() {
        try{
            InputStream is = s.getInputStream();

            DataInputStream dis = new DataInputStream(is);
            int lenObj = dis.readInt();
            byte[] objBytes = dis.readNBytes(lenObj);
            
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objBytes));
            
            Agent backAgent = (Agent) ois.readObject();
            Hashtable<String,Object> result = backAgent.getNameServer();
            System.out.println("le resultat de la recherche est " + result.get("result"));

        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("On a pas réussi à revenir");
        }

        System.out.println("fin présumée");
        
        try {
            s.close();
        } catch (IOException ex) {
            System.getLogger(Source.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }


    public static void main(String[] args) throws Exception {
        Agent agentEnvoi = new AgentCount();
        agentEnvoi.init("agent1",new Node("localhost",8081));

        Hashtable<String,Object> nameServer = new Hashtable<>();
        nameServer.put("path", Paths.get("./AgentCount.class"));
        // nameServer.put("destination", new Node("localhost",8082));
        agentEnvoi.setNameServer(nameServer);
        try {
            agentEnvoi.main();
        } catch (MoveException e) {
            e.printStackTrace();
        }


        ServerSocket s = new ServerSocket(8081);
        while (true) { new Thread(new Source(s.accept())).start(); }        
    }
        
}
