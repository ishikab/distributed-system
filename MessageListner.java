import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;


public class Listener implements Runnable {
    private ServerSocket socket; 
    private Integer port;

    private LinkedList<Thread> receiveThreads;

    public Listener(Integer port) {
        this.port = port;
        rThreads = new LinkedList<Thread>();
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(port);
            
            while(true) {
                Socket receiveSocket = socket.accept();

                Thread receiveThread = new Thread(new Receiver(receiveSocket));
                receiveThread.start();
                receiveThreads.add(receiveThread); // keep track of all of the spawned threads
            }
        }
        catch (IOException e) {
            System.err.println("Unable to listen on" + port);
        }
    }

    public void exit() throws IOException {
        while(!rThreads.isEmpty()) {
            rThreads.removeFirst().interrupt();
        }
        socket.close();
    }
}
