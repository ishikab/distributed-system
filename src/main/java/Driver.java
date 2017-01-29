import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

/**
 * user interface for demo
 */
public class Driver {
    public static void main(String[] args) throws IOException{
        System.out.println("Welcome to 18-842 distributed systems lab 0");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please enter local name::");
        String localName = br.readLine();
        
        System.out.println("Please enter the name of configuration file::");
        String configFileName = br.readLine();
        if (configFileName.equals("")) configFileName = "config.yaml";
        
        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
        while (true) {
            try {
                Message message;
                System.out.print("please input [send/receive/exit]: ");
                switch (br.readLine()) {
                    case "send":
                        message = new Message(messagePasser.getLocalName(), br);
                        messagePasser.send(message);
                        break;
                    case "receive":
                        message = messagePasser.receive();
                        if (message == null) LogUtil.log("No new message");
                        else LogUtil.log(message);
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
