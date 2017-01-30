import org.yaml.snakeyaml.Yaml;

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

/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

/**
 * Main message pass class
 */
class MessagePasser implements MessageReceiveCallback {
    private final ConcurrentHashMap<String, Node> nodeHashMap = new ConcurrentHashMap<>();
    private final LinkedList<Rule> sendRules = new LinkedList<>();
    private final LinkedList<Rule> receiveRules = new LinkedList<>();
    private final LinkedBlockingQueue<Message> sendDelayMessageQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveMessagesQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveDelayMessageQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, AtomicInteger> seqNumMap = new ConcurrentHashMap<>();
    private String configurationFileName;
    private String localName;
    private String IP;
    private Integer port;
    private MessageListenerThread listenerThread;

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
                            this.IP = (String) map.get("IP");
                            this.port = (Integer) map.get("port");
                        }
                    }
                }
            } else LogUtil.logFatalErr("configuration section not found");
            LogUtil.logInfo(String.format("[%s] %s:%s", this.localName, this.IP, this.port));
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
        listenerThread = new MessageListenerThread(this.port, this);
        listenerThread.start();
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

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
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
        try (Socket socket = new Socket(destNode.getIP(), destNode.getPort())) {
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
                        break;
                    case DROP_AFTER:
                        if (message.getSeqNum() < rule.seqNum)
                            this.receiveMessagesQueue.add(message);

                        break;
                    case DUPLICATE:
                        this.receiveMessagesQueue.add(message);
                        this.receiveMessagesQueue.add(message.clone());

                        break;
                    case DELAY:
                        this.receiveDelayMessageQueue.add(message);
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

    private void checkNodeInfo() {
        try {
            if (this.nodeHashMap.getOrDefault(this.localName, null) == null) {
                LogUtil.logFatalErr("local name not found");
            }
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (!localIP.equals(this.nodeHashMap.get(this.localName).getIP())) {
                LogUtil.logFatalErr(String.format("Localhost IP (%s) doesn't match. supposed to be (%s", localIP, this.nodeHashMap.get(this.localName).getIP()));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.logFatalErr("host not found");
            System.exit(-1);
        }
    }

    private void listReceiveRules() {
        LogUtil.logIterable("Receive Rules Info:", this.receiveRules);
    }

    private void listSendRules() {
        LogUtil.logIterable("Send Rules Info:", this.sendRules);
    }

    void listRules() {
        this.listSendRules();
        this.listReceiveRules();
    }

    void listNodes() {
        LogUtil.logIterable("Nodes Info:", this.nodeHashMap.values());

    }


}
