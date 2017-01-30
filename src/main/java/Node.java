import java.util.LinkedHashMap;

/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

/**
 * Node data structure for cluster node info
 */
class Node {
    private String name;
    private String IP;
    private Integer port;

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Node(LinkedHashMap data) {
        this.name = (String) data.get("name");
        this.IP = (String) data.get("IP");
        this.port = (Integer) data.get("port");
    }

    String getIP() {
        return IP;
    }

    Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s:%d", this.name, this.IP, this.port);
    }
}
