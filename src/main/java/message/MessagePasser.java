package message;

import clock.ClockService;
import config.Configuration;
import config.Node;
import config.Rule;
import logger.LogUtil;
import config.Group;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main message pass class
 */
public class MessagePasser implements MessageReceiveCallback {
    private final Configuration configuration = new Configuration();
    private final LinkedBlockingQueue<Message> sendDelayMessageQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveMessagesQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveDelayMessageQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, AtomicInteger> seqNumMap = new ConcurrentHashMap<>();
    ClockService clockService;
    private String localName;
    private String IP;
    private Integer port;
    private MessageListenerThread listenerThread;
    boolean block = false;

    @SuppressWarnings("unchecked")
    public MessagePasser(String configFileName, String localName) {
        this.localName = localName;
        Configuration.localName = localName;
        //this.seqNum = new AtomicInteger(0);
        configuration.updateConfiguration(configFileName);
        Node self = configuration.nodeMap.get(localName);
        LogUtil.info(self);
        this.IP = self.getIP();
        this.port = self.getPort();
        clockService = ClockService.getInstance();
//        checkNodeInfo();
        listenerThread = new MessageListenerThread(this.port, this);
        listenerThread.start();
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

    public void send(Message message) {
        boolean duplicateMessage = false;
        try {
            seqNumMap.putIfAbsent(message.getDest(), new AtomicInteger(-1));
            message.setSeqNum((seqNumMap.get(message.getDest())).incrementAndGet());
            for (Rule rule : Configuration.sendRules) {
                if (rule.matches(message)) {
                    LogUtil.println("found match: " + rule);
                    LogUtil.println(String.format("[%s] %s", rule.action, message));
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
            if (duplicateMessage) {
                Message clonedMessage = message.clone();
                clonedMessage.setDuplicate(true);
                directSend(clonedMessage);
            }

            while (sendDelayMessageQueue.peek() != null) {
                directSend(sendDelayMessageQueue.poll());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void directSend(Message message) throws IOException {
        Node destNode = Configuration.nodeMap.getOrDefault(message.getDest(), null);
        if (destNode == null) {
            LogUtil.error("dest not found");
            return;
        }
        try (Socket socket = new Socket(destNode.getIP(), destNode.getPort())) {
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                LogUtil.println(message);
                out.writeObject(message);
                out.flush();
            }
        }
    }

    @Override
    public void handleMessage(Message message) {
        //LogUtil.println(message);
        for (Rule rule : Configuration.receiveRules) {
            if (rule.matches(message)) {
                LogUtil.println("found rule match: " + rule);
                LogUtil.println(String.format("[%s] %s", rule.action, message));
                switch (rule.action) {
                    case DROP:
                        break;
                    case DROP_AFTER:
                        if (message.getSeqNum() <= rule.seqNum) {
                            this.receiveMessagesQueue.add(message);
                            this.block = false;
                        }
                        break;
                    case DUPLICATE:
                        this.receiveMessagesQueue.add(message);
                        this.receiveMessagesQueue.add(message.clone());
                        this.block = false;
                        break;
                    case DELAY:
                        this.receiveDelayMessageQueue.add(message);
                        this.block = true;
                        break;
                }
                return;
            }
        }
        this.receiveMessagesQueue.add(message);
        this.block = false;
    }

    public Message receive() {
        if (receiveMessagesQueue.peek() == null)
            return null;
        Message message = receiveMessagesQueue.poll();
        if (message != null && this.block != true)
        {
          while (this.receiveDelayMessageQueue.peek() != null) {
              this.receiveMessagesQueue.offer(this.receiveDelayMessageQueue.poll());
          }
        }
        clockService.updateTime(((TimeStampedMessage) message).getTimeStamp());
        return message;
    }

    private void checkNodeInfo() {
        try {
            if (this.configuration.nodeMap.getOrDefault(this.localName, null) == null) {
                LogUtil.fatalError("local name not found");
            }
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (!localIP.equals(this.configuration.nodeMap.get(this.localName).getIP())) {
                LogUtil.fatalError(String.format("Localhost IP (%s) doesn't match. supposed to be (%s", localIP,
                        this.configuration.nodeMap.get(this.localName).getIP()));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.fatalError("host not found");
            System.exit(-1);
        }
    }

    private void listReceiveRules() {
        LogUtil.logIterable("Receive Rules Info:", this.configuration.receiveRules);
    }

    private void listSendRules() {
        LogUtil.logIterable("Send Rules Info:", this.configuration.sendRules);
    }

    public void listRules() {
        this.listSendRules();
        this.listReceiveRules();
    }

    public void listNodes() {
        LogUtil.logIterable("Nodes Info:", this.configuration.nodeMap.values());

    }

    public void updateConfiguration() {
        this.configuration.updateConfiguration();
    }

    public void listGroups() {
        LogUtil.logIterable("Groups Info:", Configuration.groupMap.values());
    }
   
    public void multicast(Message msg) throws InterruptedException {
      GroupMessage grpMsg = (GroupMessage) msg;
      Group group = this.configuration.groupMap.get(grpMsg.getGroupName()); 
     
      for (String dest : group.getGroupMembers()) {
        if (!dest.equals(localName)) {
            GroupMessage sendMsg = new GroupMessage(grpMsg.getDest(), grpMsg.getKind(), grpMsg.getData());
            sendMsg.setDest(dest);
            send(sendMsg);
        }
        else {
            GroupMessage sendMsg = new GroupMessage(grpMsg.getDest(), grpMsg.getKind(), grpMsg.getData());
            //Handle send to self here
        }
      }      
    }
}
