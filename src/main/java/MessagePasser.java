import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by chenxiw on 1/23/17.
 * chenxi.wang@sv.cmu.edu
 */
class MessagePasser {
    private String configurationFileName;
    private String localName;
    private String ip;
    private Integer port;
    private ConcurrentHashMap<String, Node> nodeHashMap;
    private LinkedBlockingQueue<Rule> sendRulesQueue;
    private LinkedBlockingQueue<Rule> receiveRulesQueue;

    @SuppressWarnings("unchecked")
    MessagePasser(String configurationFilename, String localName) {
        this.configurationFileName = configurationFilename;
        this.localName = localName;
        this.nodeHashMap = new ConcurrentHashMap<>();
        this.sendRulesQueue = new LinkedBlockingQueue();
        this.receiveRulesQueue = new LinkedBlockingQueue();
        HashMap<String, ArrayList> dataMap = readConfiguration(configurationFilename);
        if (dataMap != null) {
            ArrayList<LinkedHashMap<String, Object>> nodeConfig = dataMap.getOrDefault("configuration", null);
            if (nodeConfig != null) {
                for (LinkedHashMap map : nodeConfig) {
                    String nodeName = (String) map.get("name");
                    if (nodeName != null) {
                        this.nodeHashMap.put(nodeName, new Node(map));
                        if(nodeName.equals(localName)) {
                            this.ip = (String) map.get("ip");
                            this.port = (Integer) map.get("port");
                        }
                    }
                }
            } else LogUtil.logFatalErr("Configuration section not found");
            LogUtil.log(String.format("ip: %s:%s", this.ip, this.port));
            ArrayList<LinkedHashMap<String, Object>> sendRulesConfig = dataMap.getOrDefault("sendRules", null);
            if (sendRulesConfig != null) {
                this.sendRulesQueue.addAll(sendRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
            ArrayList<LinkedHashMap<String, Object>> receiveRulesConfig = dataMap.getOrDefault("receiveRules", null);
            if (receiveRulesConfig != null) {
                this.receiveRulesQueue.addAll(receiveRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
        }
        LogUtil.log("Load Finished.");
        checkNodeInfo();
        new MessageListenerThread(this.port).start();
    }

    void send(Message message) {

    }

    Message receive() {
        return new Message("", "", "");
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, ArrayList> readConfiguration(String config) {
        try {
            FileInputStream fileInputStream = new FileInputStream(config);
            return (HashMap<String, ArrayList>) new Yaml().load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkNodeInfo(){
        try {
            if(this.nodeHashMap.getOrDefault(this.localName, null)==null) {
                LogUtil.logFatalErr("local name not found");
            }
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (!localIP.equals(this.nodeHashMap.get(this.localName).getIp())){
                LogUtil.logFatalErr(String.format("Localhost IP (%s) doesn't match. supposed to be (%s", localIP, this.nodeHashMap.get(this.localName).getIp()));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.logFatalErr("Host not found");
            System.exit(-1);
        }
    }
}