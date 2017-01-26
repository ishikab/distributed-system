package DistSyslab0;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;


public class MessagePasser
{
  private String configFileName;
  private String localName;
  
  public ArrayList<Rule> sendRules = null;
  public ArrayList<Rule> reciveRules = null;
  
  private LinkedBlockingQueue<Message> sendQueue;
  private LinkedBlockingQueue<Message> sendDelayQueue;
  private LinkedBlockingQueue<Message> receiveQueue;
  private LinkedBlockingQueue<Message> receiveDelayQueue;

  private Integer sequenceNumber;
  private HashMap<String, Node> nodeInfo = new HashMap<String, Node>();

  public MessagePasser(String configuration_filename, String local_name)
  {
    nodeInfo = ParseConfig.PopulateNodeInfo();
  }

  public void send(Message message)
  {
    message.SetSource(localName);
    message.SetSeqNum(sequenceNumber);//should be atomic
    //read the send rules
    //can keep a timestamp for the config file update and read only when updated
  }

  public Message receive()
  {
    Message message = null;
        if(receiveQueue.size() > 0) {
            message = receiveQueue.poll();
        }
        return message;
  }
  
  public Boolean NodeExists()
  {
     return true;
  } 
}
