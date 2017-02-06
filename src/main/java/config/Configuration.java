package config;

import logger.LogUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Configuration {
    public static final ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<>();
    public static final LinkedList<Rule> sendRules = new LinkedList<>();
    public static final LinkedList<Rule> receiveRules = new LinkedList<>();
    public static String localName;
    private static Integer numNodes = null;
    private String configurationFileName;

    public Configuration(String fileName) {
        this.configurationFileName = fileName;
        updateConfiguration();
    }

    public Configuration() {

    }

    public static Integer getNumNodes() {
        if (numNodes == null)
            LogUtil.fatalError("Read num nodes before initialization");
        return numNodes;
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

    public void updateConfiguration(String configurationFileName) {
        this.configurationFileName = configurationFileName;
        updateConfiguration();
    }

    @SuppressWarnings("unchecked")
    public void updateConfiguration() {
        nodeMap.clear();
        sendRules.clear();
        receiveRules.clear();
        HashMap<String, ArrayList> dataMap = readConfiguration(this.configurationFileName);
        if (dataMap != null) {
            ArrayList<LinkedHashMap<String, Object>> nodeConfig = dataMap.getOrDefault("configuration", null);
            if (nodeConfig != null) {
                for (LinkedHashMap map : nodeConfig) {
                    String nodeName = (String) map.get("name");
                    if (nodeName != null) {
                        nodeMap.put(nodeName, new Node(map));
                    }
                }
                numNodes = nodeMap.size();
            } else LogUtil.fatalError("configuration section not found");
            ArrayList<LinkedHashMap<String, Object>> sendRulesConfig = dataMap.getOrDefault("sendRules", null);
            if (sendRulesConfig != null) {
                sendRules.addAll(sendRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
            ArrayList<LinkedHashMap<String, Object>> receiveRulesConfig = dataMap.getOrDefault("receiveRules", null);
            if (receiveRulesConfig != null) {
                receiveRules.addAll(receiveRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
        }
    }
}
