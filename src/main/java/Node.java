import java.util.LinkedHashMap;

/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

class Node {
    private String name;
    private String ip;
    private Integer port;

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    Node(LinkedHashMap data) {
        this.name = (String) data.get("name");
        this.ip = (String) data.get("ip");
        this.port = (Integer) data.get("port");
    }
}
