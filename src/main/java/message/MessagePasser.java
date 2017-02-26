package message;

import clock.ClockService;
import config.Configuration;
import config.Node;
import config.Rule;
import logger.LogUtil;
import config.Group;
import multicast.MulticastCoordinator;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Main message pass class
 */
public class MessagePasser implements MessageReceiveCallback {
    private final Configuration configuration = new Configuration();
    private final LinkedBlockingQueue<Message> sendDelayMessageQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveMessagesQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Message> receiveDelayMessageQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, AtomicInteger> seqNumMap = new ConcurrentHashMap<>();
    private MulticastCoordinator multicastCoordinator = null;
    private ClockService clockService;
    private String localName;
    private String IP;
    private Integer port;
    private MessageListenerThread listenerThread;
    private boolean block = false;
    private ArrayList<Message> multicastReceived = new ArrayList<Message>();

    @SuppressWarnings("unchecked")
    public MessagePasser(String configFileName, String localName) {
        this.localName = localName;
        Configuration.localName = localName;
        //this.seqNum = new AtomicInteger(0);

        configuration.updateConfiguration(configFileName);
        Node self = Configuration.getNodeMap().get(localName);
        LogUtil.info(self);
        this.IP = self.getIP();
        this.port = self.getPort();
        clockService = ClockService.getInstance();
        multicastCoordinator = MulticastCoordinator.getInstance();
        multicastCoordinator.init();
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
        seqNumMap.putIfAbsent(message.getDest(), new AtomicInteger(-1));
        message.setSeqNum((seqNumMap.get(message.getDest())).incrementAndGet());
        LogUtil.debug("trying to send " + message);        
        for (Rule rule : Configuration.getSendRules()) {
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

    }

    private void directSend(Message message) {
        Node destNode = Configuration.getNodeMap().getOrDefault(message.getDest(), null);
        if (destNode == null) {
            LogUtil.error("dest not found");
            return;
        }
        try {
            try (Socket socket = new Socket(destNode.getIP(), destNode.getPort())) {
                try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
                    out.writeObject(message);
                    out.flush();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            if (message instanceof GroupMessage)
            {
                String error = destNode.getName() + " is not live";
                LogUtil.error(error);
            }
            else
            LogUtil.error("failed to send message");
        }
    }

    @Override
    public void handleMessage(Message message) {
        //LogUtil.println(message);
        for (Rule rule : Configuration.getReceiveRules()) {
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

    /**
     * On B-deliver from p_j (j!=i), with g = group(m)
     * place <v_j, m> in hold-back queue
     * @param message
     */
    private void handleGroupMessage(GroupMessage message) {
        multicastCoordinator.holdMessage(message);
    }

    public Message receive() {
        Message message = null;
        boolean multi = false;
        boolean nonMulti = false;

        if(multicastCoordinator.holdBackQueue.size() > 0) {
            for(int i = 0; i < multicastCoordinator.holdBackQueue.size(); i++) {
                if (multicastCoordinator.releaseGroupMessage(multicastCoordinator.holdBackQueue.get(i)) && !multi) {
                    message = multicastCoordinator.holdBackQueue.get(i);
                    multicastCoordinator.holdBackQueue.remove(i);
                    multi = true;
                }
            }
        }
        if (receiveMessagesQueue.peek() == null)
            return null;
        while((receiveMessagesQueue.size() > 0) && !multi && !nonMulti) {
            message = receiveMessagesQueue.poll();
            if (message instanceof GroupMessage) {
                if (!multicastMessageWasReceived(message)) {
                    multicastReceived.add(message);
                    if(!localName.equalsIgnoreCase(message.getSrc())) {
                        this.recast((GroupMessage) message);
                    }
                    if (multicastCoordinator.releaseGroupMessage((GroupMessage) message)) {
                        multi = true;
                        multicastCoordinator.updateTime(((GroupMessage) message).getGroupName(), (GroupMessage) message);
                    }
                    else {
                        handleGroupMessage((GroupMessage) message);
                        message = null;
                    }
                }
                else {
                    message = null;
                }
            }
            else {
                nonMulti = true;
            }
        }

        if (message != null && this.block != true) {
            while (this.receiveDelayMessageQueue.peek() != null) {
                this.receiveMessagesQueue.offer(this.receiveDelayMessageQueue.poll());
            }
        }
        if (message != null) {
            clockService.updateTime(((TimeStampedMessage) message).getTimeStamp());
        }
        return message;
    }

    private void checkNodeInfo() {
        try {
            if (Configuration.getNodeMap().getOrDefault(this.localName, null) == null) {
                LogUtil.fatalError("local name not found");
            }
            String localIP = InetAddress.getLocalHost().getHostAddress();
            if (!localIP.equals(Configuration.getNodeMap().get(this.localName).getIP())) {
                LogUtil.fatalError(String.format("Localhost IP (%s) doesn't match. supposed to be (%s", localIP,
                        Configuration.getNodeMap().get(this.localName).getIP()));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.fatalError("host not found");
            System.exit(-1);
        }
    }

    private void listReceiveRules() {
        LogUtil.logIterable("Receive Rules Info:", Configuration.getReceiveRules());
    }

    private void listSendRules() {
        LogUtil.logIterable("Send Rules Info:", Configuration.getSendRules());
    }

    public void listRules() {
        this.listSendRules();
        this.listReceiveRules();
    }

    public void listNodes() {
        LogUtil.logIterable("Nodes Info:", Configuration.getNodeMap().values());

    }

    public void updateConfiguration() {
        this.configuration.updateConfiguration();
    }

    public void listGroups() {
        LogUtil.logIterable("Groups Info:", Configuration.getGroupMap().values());
    }

    /**
     * To CO-multicast message m to group g (Figure 15.15, P657)
     * @param groupMessage message to multicast
     */
    public void multicast(GroupMessage groupMessage) {
        String groupName = groupMessage.getGroupName();
        Group group = Configuration.getGroupMap().get(groupName);
        if (group == null || !group.hasNodeName(localName)) {
            // TODO: when node is not in that group, return. maybe we need to change it later
            LogUtil.error(localName + " is not in group " + groupName);
            return;
        }
        multicastCoordinator.incrementTime(groupName, localName); // V_i[i] = v_i[i] + 1
        for (String dest : group.getGroupMembers()) {
            //if (!dest.equals(localName)) {
                GroupMessage sendGroupMessage = new GroupMessage(groupMessage);
                sendGroupMessage.setDest(dest);
                sendGroupMessage.setCurrentGroupTimeStamp(groupName);
                this.send(sendGroupMessage);
            //}
        }
    }

    public void recast(GroupMessage groupMessage) {
        String groupName = groupMessage.getGroupName();
        Group group = Configuration.getGroupMap().get(groupName);
        if (group == null || !group.hasNodeName(localName)) {
            // TODO: when node is not in that group, return. maybe we need to change it later
            LogUtil.error(localName + " is not in group " + groupName);
            return;
        }
        //multicastCoordinator.incrementTime(groupName, localName); // V_i[i] = v_i[i] + 1
        for (String dest : group.getGroupMembers()) {
            if (!localName.equals(groupMessage.getSrc())) {
                GroupMessage recastGroupMessage = groupMessage.clone();
                recastGroupMessage.setDest(dest);
                //seqNumMap.putIfAbsent(dest, new AtomicInteger(-1));
                //recastGroupMessage.setSeqNum((seqNumMap.get(dest)).incrementAndGet());
                this.directSend(recastGroupMessage);
            }
        }
    }

  private boolean multicastMessageWasReceived(Message message) {
        boolean wasReceived = false;
        ListIterator<Message> it = multicastReceived.listIterator();
        for(Message received : multicastReceived) {
            if(received.isSameAs(message)) {
                wasReceived = true;
            }
        }
        return wasReceived;
    }

}
