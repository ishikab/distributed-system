package DistSyslab0;

import org.yaml.snakeyaml.Yaml;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.Map;
import java.util.List;
import java.io.*;

public class ParseConfig
{
  @SuppressWarnings("unchecked")

  
  public static HashMap<String, Node> nodeInfo = new HashMap<String, Node>();
  public ArrayList<Rule> sendRulesList = null;
  public ArrayList<Rule> receiveRulesList = null;


  public void readFile(String configFile) throws FileNotFoundException
  {
    Yaml yaml = new Yaml();
    FileInputStream configFileStream = new FileInputStream(configFile);
    Map<String, Object> configMap = (Map<String, Object>) yaml.load(configFileStream);
    // check for error conditions
    
    List<Map<String, Object>> sendRuleList = (List<Map<String, Object>>)
					configMap.get("sendRules");
    // check for empty rules
    for (Map<String, Object> iterator : sendRuleList) 
    {
      Rule rule = new Rule();
      String action = (String)iterator.get("action");
      String dest = (String)iterator.get("dest");
      String src = (String)iterator.get("src");
      String kind = (String)iterator.get("kind");
      Integer seqNum = (Integer)iterator.get("seqNum");

      rule.SetAction(action);
      rule.SetDestination(dest);
      rule.SetSource(src);
      rule.SetKind(kind);
      rule.SetSeqNumber(seqNum);
      
      sendRulesList.add(rule); 
    }
    
  }

  public static HashMap<String, Node> PopulateNodeInfo()
  {
    return nodeInfo;
  }
  
  public ArrayList<Rule> readSendRules()
  {
    return sendRulesList;
  }
  
  public ArrayList<Rule> readReceiveRules()
  {
    return receiveRulesList;
  }

  
}
