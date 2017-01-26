import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Receiver implements Runnable {
    private Socket socket;
    private InetAddress incomingAddress;
    private boolean senderDead;
    
    private ArrayList<Rule> rRules;
    private LinkedBlockingQueue<Message> rQueue;
    private LinkedBlockingQueue<Message> rDelayQueue;
    private Message message = null;
    
    private ObjectInputStream incoming;

    public Receiver(Socket socket, 
                        LinkedBlockingQueue<Message> rQueue, 
                        LinkedBlockingQueue<Message> rDelayQueue) {
                            
        this.socket = socket;
        this.rQueue = rQueue;
        this.rDelayQueue = rDelayQueue;
        senderDead = false;
    }

    @Override
    public void run() {
        try {
            incoming = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            incomingAddress = socket.getInetAddress();

        } catch (IOException e) {
            System.err.println("RECEIVER ERROR: Unable to open input stream.");
            System.exit(0);
        }
        
        while(true) {
            message = null;
            try {
                message = (Message) incoming.readObject();
                senderDead = false;
            } catch (IOException e) {
                if(!senderDead) {
                    System.out.println("RECEIVER ERROR: Sender disconnected @" + incomingAddress.toString());                    
                }       
                senderDead = true;
                
            } catch (ClassNotFoundException e) {
                System.err.println("RECEIVER ERROR: Object class could not be determined.");
            }            
            
            
            if(message != null) {
                
                rRules = Utility.readReceiveRules();
                Action action = Action.NIL;
                int actionSeqNum = -1;
                int messageSeqNum = message.getSeqNum();
                for (Rule rule : rRules) {
                    if ((Utility.ruleApplies(message, rule)) && (action == Action.NIL)) {
                        action = rule.getAction();
                        actionSeqNum = rule.getSeqNum();
                    }
                }
    
                synchronized(rQueue) {
                    // Perform action according to the matched rule's type.
                        if (action.equals("drop")
                        {
                          continue;
                        }
                        else if (action.equals("dropAfter")
                        {
                            if(messageSeqNum < actionSeqNum) 
                            {
                                rQueue.add(message);
                            }
                        }
                        else if (action.equals("delay"))
                        {
                            rDelayQueue.add(message);
                        }
                        else if (action.equals("duplicate"))
                        {
                          Message dupMsg = message.duplicate();
                          dupMsg.SetDuplicate(true);
                          rQueue.add(dupMsg);
                          rQueue.add(message);
                        }
                        else
                        {
                            rQueue.add(message);
                        }
                    
                    if(rQueue.size() > 0) {
                        while(!rDelayQueue.isEmpty()) {
                            rQueue.add(rDelayQueue.poll());
                        }
                    }
                }
            }
        }
    }

    public void terminate() throws IOException {
        socket.close();
    }
}

