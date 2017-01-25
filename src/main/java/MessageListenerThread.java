import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chenxiw on 1/24/17.
 * chenxi.wang@sv.cmu.edu
 */
public class MessageListenerThread extends Thread {
    private ServerSocket serverSocket;
    Integer port;

    MessageListenerThread(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            while (true) {
                Socket socket = serverSocket.accept();
                Message message = (Message) new ObjectInputStream(new BufferedInputStream(socket.getInputStream())).readObject();
                LogUtil.log(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.logFatalErr("failed to listen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
