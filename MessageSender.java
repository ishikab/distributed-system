import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sender implements Runnable {
    private HashMap<String, ObjectOutputStream> outputStreamMap;
    
    private HashMap<String, Node> nodeMap;
    private LinkedBlockingQueue<Message> sQueue;
    private LinkedBlockingQueue<Message> sDelayQueue;
    private LinkedBlockingQueue<Message> finalSendQueue;

    public Sender(HashMap<String, Node> nodeMap, LinkedBlockingQueue<Message> sQueue, LinkedBlockingQueue<Message> sDelayQueue) {
        this.nodeMap = nodeMap;
        this.sQueue = sQueue;
        this.sDelayQueue = sDelayQueue;
        oosMap = new HashMap<String, ObjectOutputStream>();
    }

    @Override
    public void run() {
        while(true) {
            if(sQueue.size() > 0) {
                
                finalSendQueue = new LinkedBlockingQueue<Message>();
                while(!sQueue.isEmpty()) {
                    finalSendQueue.add(sQueue.poll());
                }
                while(!sDelayQueue.isEmpty()) {
                    finalSendQueue.add(sDelayQueue.poll());
                }
                
                while(!finalSendQueue.isEmpty()) {
                    Message message = finalSendQueue.poll();
                    
                    Node destination = nodeMap.get(message.getDestination());
                    String IP = destination.getIP();
                    Integer port = destination.getPort();
                    
                    if(!oosMap.containsKey(destination.getName())) {
                        try {
                            Socket socket = new Socket(IP, port);
                            
                            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                            oosMap.put(destination.getName(), oos);
                        } 
                        catch(Exception e) {
                            System.err.println("SENDER ERROR: Connection to " + destination.getName() + " could not be made.");                             
                        }
                    }

                    ObjectOutputStream sendTo = oosMap.get(destination.getName());

                    try {
                        sendTo.writeObject(message);
                        sendTo.flush();                        
                    }
                    catch (IOException e) {
                        System.err.println("SENDER ERROR: Output stream to " + destination.getName() + " could not be instantiated.");
                    }                    
                }               
            }
        }
    }

    public void terminate() throws IOException {
        for(Socket s : socketMap.values()) {
            s.close();
        }
        return;
    }
}
