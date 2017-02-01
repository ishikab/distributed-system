package config;

import java.util.LinkedHashMap;

/**
 * config.Node data structure for cluster node info
 */
public class Node {
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

    public String getIP() {
        return IP;
    }

    public Integer getPort() {
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
