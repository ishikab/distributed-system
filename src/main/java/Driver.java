import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by chenxiw on 1/23/17.
 * chenxi.wang@sv.cmu.edu
 */
public class Driver {
    public static void main(String[] args) throws IOException{
        System.out.println("Welcome to 18-842 distributed systems lab 0");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please enter local name::");
        String localName = br.readLine();
        
        System.out.println("Please enter the name of configuration file::");
        String configFileName = br.readLine();
        
        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
        while (true) {
            try {
                Message msg;
                System.out.print("please input [send/receive/exit]: ");
                switch (br.readLine()) {
                    case "send":
                        msg = new Message(messagePasser.getLocalName(), br);
                        messagePasser.send(msg);
                        break;
                    case "receive":
                        msg = messagePasser.receive();
                        if (msg == null) LogUtil.log("No new message");
                        else LogUtil.log(msg);
                        break;
                    case "exit":
                        LogUtil.log("Thanks for using");
                        System.exit(0);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
