package sourceServer;


public class Node implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private String nameServ;
    private int nbPort;

    public Node(String nameServ, int nbPort){
        this.nameServ = nameServ;
        this.nbPort = nbPort;
    }

    public String getNameServ() {
        return nameServ;
    }

    public int getNbPort() {
        return nbPort;
    }

    public boolean equals(Node autre){
        return autre.nameServ.equals(nameServ) && autre.nbPort == (nbPort);
    }

    @Override
    public String toString() {
        return "le node de nom " + getNameServ() + " au port " + String.valueOf(getNbPort());
    }
}