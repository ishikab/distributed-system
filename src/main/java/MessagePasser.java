import org.yaml.snakeyaml.Yaml;
import sun.rmi.runtime.Log;

import javax.security.auth.callback.Callback;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */
class MessagePasser implements MessageReceiveCallback {
    private String configurationFileName;
    private String localName;
    private String ip;
    private Integer port;
    private final ConcurrentHashMap<String, Node> nodeHashMap = new ConcurrentHashMap<>();
    private LinkedList<Rule> sendRules = new LinkedList<>();
    private LinkedList<Rule> receiveRules = new LinkedList<>();
    private LinkedBlockingQueue<Message> sendMessageQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> sendDelayMessageQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> receiveMessagesQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> receiveDelayMessageQueue = new LinkedBlockingQueue<>();
    private ConcurrentHashMap<String, AtomicInteger> seqNumMap = new ConcurrentHashMap<>();
    private MessageListenerThread listenerThread;

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
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

    @SuppressWarnings("unchecked")
    MessagePasser(String configurationFilename, String localName) {
        this.configurationFileName = configurationFilename;
        this.localName = localName;
        //this.seqNum = new AtomicInteger(0);
        HashMap<String, ArrayList> dataMap = readConfiguration(configurationFilename);
        if (dataMap != null) {
            ArrayList<LinkedHashMap<String, Object>> nodeConfig = dataMap.getOrDefault("configuration", null);
            if (nodeConfig != null) {
                for (LinkedHashMap map : nodeConfig) {
                    String nodeName = (String) map.get("name");
                    if (nodeName != null) {
                        this.nodeHashMap.put(nodeName, new Node(map));
                        if (nodeName.equals(localName)) {
                            this.ip = (String) map.get("ip");
                            this.port = (Integer) map.get("port");
                        }
                    }
                }
            } else LogUtil.logFatalErr("configuration section not found");
            LogUtil.log(String.format("ip: %s:%s", this.ip, this.port));
            ArrayList<LinkedHashMap<String, Object>> sendRulesConfig = dataMap.getOrDefault("sendRules", null);
            if (sendRulesConfig != null) {
                this.sendRules.addAll(sendRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
            ArrayList<LinkedHashMap<String, Object>> receiveRulesConfig = dataMap.getOrDefault("receiveRules", null);
            if (receiveRulesConfig != null) {
                this.receiveRules.addAll(receiveRulesConfig.stream().map(Rule::new).collect(Collectors.toList()));
            }
        }
        checkNodeInfo();
        listenerThread = new MessageListenerThread(this.port, this.receiveRules, this);
        listenerThread.start();
    }

    void send(Message message) {
        boolean duplicateMessage = false;
        try {
            seqNumMap.putIfAbsent(message.getDest(), new AtomicInteger(-1));
            message.setSeqNum((seqNumMap.get(message.getDest())).incrementAndGet());
            for (Rule rule : sendRules) {
                if (rule.matches(message)) {
                    LogUtil.log("found match: " + rule);
                    LogUtil.log(String.format("[%s] %s", rule.action, message));
                    switch (rule.action) {
                        case DROP:
                            return;
                        case DROP_AFTER:
                            if (message.getSeqNum() > rule.seqNum)
                                return;
                            break;
                        case DUPLICATE:
                            duplicateMessage = true;
                            break;
                        case DELAY:
                            this.sendDelayMessageQueue.add(message);
                            return;
                    }
                    break;
                }
            }
            directSend(message);
            if (duplicateMessage)
                directSend(message.clone());
            while (sendDelayMessageQueue.peek() != null) {
                directSend(sendDelayMessageQueue.poll());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void directSend(Message message) throws IOException {
        Node destNode = this.nodeHashMap.getOrDefault(message.getDest(), null);
        if (destNode == null) {
            LogUtil.logErr("dest not found");
            return;
        }
        try (Socket socket = new Socket(destNode.getIp(), destNode.getPort())) {
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                LogUtil.log(message);
                out.writeObject(message);
                out.flush();
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        //LogUtil.log(message);
        for (Rule rule : this.receiveRules) {
            if (rule.matches(message)) {
                LogUtil.log("found rule match: " + rule);
                LogUtil.log(String.format("[%s] %s", rule.action, message));
                switch (rule.action) {
                    case DROP:
                        if (rule.isDuplicate()) {
                          if (!message.isDuplicate())
                            this.receiveMessagesQueue.add(message);
                        }
                        break;
                    case DROP_AFTER:
                        if (rule.isDuplicate() == false) {
                          if (message.getSeqNum() < rule.seqNum)
                              this.receiveMessagesQueue.add(message);
                        }
                        else {
                          if (message.isDuplicate()) {
                            if (message.getSeqNum() < rule.seqNum)
                              this.receiveMessagesQueue.add(message);
                          }
                          else {
                            this.receiveMessagesQueue.add(message);
                          }
                        }
                        break;
                    case DUPLICATE:
                        if (rule.isDuplicate() == false) {
                          this.receiveMessagesQueue.add(message);
                          this.receiveMessagesQueue.add(message.clone());
                        }
                        else {
                          if (message.isDuplicate()) {
                            this.receiveMessagesQueue.add(message);
                            this.receiveMessagesQueue.add(message.clone());
                          }
                        }
                        break;
                    case DELAY:
                        if (rule.isDuplicate() == false) {
                          this.receiveDelayMessageQueue.add(message); 
                        }
                        else {
                          if (message.isDuplicate()) {
                            this.receiveDelayMessageQueue.add(message); 
                          }
                          else {
                             this.receiveMessagesQueue.add(message);
                          }
                        }
                        break;
                }
                return;
            }
        }
        this.receiveMessagesQueue.add(message);
    }

    Message receive() {
        if (receiveMessagesQueue.peek() == null)
            return null;
        Message message = receiveMessagesQueue.poll();
        while (this.receiveDelayMessageQueue.peek() != null) {
            this.receiveMessagesQueue.offer(this.receiveDelayMessageQueue.poll());
        }
        return message;
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

    private void checkNodeInfo() {
        try {
            if (this.nodeHashMap.getOrDefault(this.localName, null) == null) {
                LogUtil.logFatalErr("local name not found");
            }
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (!localIP.equals(this.nodeHashMap.get(this.localName).getIp())) {
                LogUtil.logFatalErr(String.format("Localhost IP (%s) doesn't match. supposed to be (%s", localIP, this.nodeHashMap.get(this.localName).getIp()));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.logFatalErr("host not found");
            System.exit(-1);
        }
    }
}
