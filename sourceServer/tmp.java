package sourceServer;
import java.util.Hashtable;

public class tmp {
    public static void main(String[] args) {
        System.out.println("debutMain");
        Hashtable<String,Object> hashtable = new Hashtable<String,Object>();
        hashtable.put("path", "23");
        hashtable.put("valeur",12);

        String path = (String) hashtable.get("path");
        System.out.println(path);

        Node n1 = new Node("a",9091);
        Node n2 = new Node("a",9092);
        System.out.println(n1.equals(n2));
    }
}