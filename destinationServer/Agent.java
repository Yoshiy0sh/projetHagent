
import java.util.*;

public interface Agent extends java.io.Serializable {
	public void init(String name, Node origin, byte[] agentClassBytes);
	public void setNameServer(Hashtable<String,Object> ns);
	public Hashtable<String,Object> getNameServer();
	public void move(Node target) throws MoveException;
	public void back() throws MoveException;
	public void main() throws MoveException;
	public void setAgentClassBytes(byte[] agentClassBytes);
}