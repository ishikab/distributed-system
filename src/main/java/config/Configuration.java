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
    public final ConcurrentHashMap<String, Node> nodeMap = new ConcurrentHashMap<>();
    public final LinkedList<Rule> sendRules = new LinkedList<>();
    public final LinkedList<Rule> receiveRules = new LinkedList<>();
    private String configurationFileName;

    public Configuration(String fileName) {
        this.configurationFileName = fileName;
        updateConfiguration();
    }

    public Configuration() {

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
        this.nodeMap.clear();
        this.sendRules.clear();
        this.receiveRules.clear();
        HashMap<String, ArrayList> dataMap = readConfiguration(this.configurationFileName);
        if (dataMap != null) {
            ArrayList<LinkedHashMap<String, Object>> nodeConfig = dataMap.getOrDefault("configuration", null);
            if (nodeConfig != null) {
                for (LinkedHashMap map : nodeConfig) {
                    String nodeName = (String) map.get("name");
                    if (nodeName != null) {
                        this.nodeMap.put(nodeName, new Node(map));
                    }
                }
            } else LogUtil.fatalError("configuration section not found");
            ArrayList<LinkedHashMap<String, Object>> sendRulesConfig = dataMap.getOrDefault("sendRules", null);
            if (sendRulesConfig != null) {
                this.sendRules.addAll(sendRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
            ArrayList<LinkedHashMap<String, Object>> receiveRulesConfig = dataMap.getOrDefault("receiveRules", null);
            if (receiveRulesConfig != null) {
                this.receiveRules.addAll(receiveRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
        }
    }
}
