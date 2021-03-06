package config;

import java.util.LinkedHashMap;

/**
 * config.Node data structure for cluster node info
 */
public class Node {
    private static Integer nodeIdGenerator = 0;
    private String name;
    private String IP;
    private Integer port;
    private Integer nodeId;

    Node(LinkedHashMap data) {
        this.name = (String) data.get("name");
        this.IP = (String) data.get("ip");
        this.port = (Integer) data.get("port");
        this.nodeId = nodeIdGenerator;
        nodeIdGenerator++;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("[%d][%s] %s:%d", this.nodeId, this.name, this.IP, this.port);
    }
}
