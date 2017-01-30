import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

class MessageListenerThread extends Thread {
    private Integer port;
    private ServerSocket serverSocket;
    private MessageReceiveCallback messageReceiveCallback;

    MessageListenerThread(Integer port, MessageReceiveCallback callback) {
        this.port = port;
        this.messageReceiveCallback = callback;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            while (true) {
                Socket socket = serverSocket.accept();
                Message message = (Message) new ObjectInputStream(new BufferedInputStream(socket.getInputStream())).readObject();
                messageReceiveCallback.handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.logFatalErr("failed to listen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
