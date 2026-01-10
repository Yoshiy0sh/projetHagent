import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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
            // byte[] objBytes = dis.readNBytes(lenObj);
            byte[] objBytes = new byte[lenObj];
            dis.readFully(objBytes);
            
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objBytes));
            
            Agent backAgent = (Agent) ois.readObject();
            Hashtable<String,Object> result = backAgent.getNameServer();
            int resultat = (int) result.get("result1") + (int) result.get("result2");
            System.out.println("le resultat de la recherche est " + resultat);

        } catch(IOException | ClassNotFoundException e){
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
        long start = System.currentTimeMillis();
        Agent agentEnvoi = new AgentCount();

        //rechercher le .class de 
        byte[] classBytes = Files.readAllBytes(Paths.get("./AgentCount.class"));

        agentEnvoi.init("agent1",new Node("localhost",8081),classBytes);

        try {
            agentEnvoi.main();
        } catch (MoveException e) {
            e.printStackTrace();
        }


        ServerSocket s = new ServerSocket(8081);
        new Source(s.accept()).run();
        long end = System.currentTimeMillis();

        System.out.println("Tems en ms : " + (end-start));
    }
        
}
