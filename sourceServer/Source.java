import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Hashtable;




public class Source {

    public static void main(String[] args) throws Exception {
        ServerSocket s = new ServerSocket(8081);


        Agent agentEnvoi = new AgentCount();
        agentEnvoi.init("agent1",new Node("localhost",8081));

        Hashtable<String,Object> nameServer = new Hashtable<String,Object>();
        nameServer.put("path", Paths.get("./AgentCount.class"));
        // nameServer.put("destination", new Node("localhost",8082));
        agentEnvoi.setNameServer(nameServer);
        try {
            agentEnvoi.main();
        } catch (MoveException e) {
            e.printStackTrace();
        }

        //code de réception
        Socket socketReception = s.accept();
        System.out.println("fin présumée");
        s.close();
        
    }
}
