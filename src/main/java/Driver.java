import logger.LogUtil;
import message.Message;
import message.MessagePasser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * user interface for demo
 */
public class Driver {
    public static void main(String[] args) throws IOException {
        LogUtil.log("Welcome to 18-842 distributed systems lab 0");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String localName, configFileName;
        if (args.length == 0) {
            System.out.print("Please enter local name:: ");
            localName = br.readLine();
            System.out.print("Please enter the name of configuration file:: ");
            configFileName = br.readLine();
            if (configFileName.equals("")) {
                LogUtil.logInfo("using default config file ./config.yaml");
                configFileName = "config.yaml";
            }
        } else {
            localName = args[1];
            configFileName = args[0];
            LogUtil.logInfo("Reading from command line args");
        }
        Logger logger = LoggerFactory.getLogger(Driver.class);
        logger.info("hello");
        logger.error("world");
        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
        while (true) {
            try {
                Message message;
                System.out.print(">>> ");
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
                    case "rules":
                        messagePasser.listRules();
                        break;
                    case "nodes":
                        messagePasser.listNodes();
                        break;
                    case "update":
                        messagePasser.updateConfiguration();
                        break;
                    default:
                        LogUtil.log("available commands: send/receive/exit/rules/nodes");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
