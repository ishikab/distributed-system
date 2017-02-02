import clock.ClockCoordinator;
import clock.LogicalTimeStamp;
import logger.LogUtil;
import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * user interface for demo
 */
public class Driver {
    public static void main(String[] args) throws IOException {
        LogUtil.log("Welcome to 18-842 Distributed Systems lab project");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String localName, configFileName;
        if (args.length == 0) {
            System.out.print("Please enter local name:: ");
            localName = br.readLine();
            System.out.print("Please enter the name of configuration file:: ");
            configFileName = br.readLine();
            if (configFileName.equals("")) {
                LogUtil.info("using default config file ./config.yaml");
                configFileName = "config.yaml";
            }
            System.out.print("Please enter clock type [logical/vector]:: ");
            String mode = br.readLine();
            if(mode.toLowerCase().startsWith("logical"))
                ClockCoordinator.setClockType(ClockCoordinator.ClockType.LOGICAL);
            else ClockCoordinator.setClockType(ClockCoordinator.ClockType.VECTOR);
        } else {
            localName = args[1];
            configFileName = args[0];
            LogUtil.info("Reading from command line args");
        }
        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
        ClockCoordinator clockCoordinator = ClockCoordinator.getInstance();
        while (true) {
            try {
                Message message;
                System.out.print(">>> ");
                switch (br.readLine()) {
                    case "send":
                        message = new TimeStampedMessage(messagePasser.getLocalName(), br);
                        messagePasser.send(message);
                        break;
                    case "receive":
                        message = messagePasser.receive();
                        if (message instanceof TimeStampedMessage)
                            clockCoordinator.updateTime(((TimeStampedMessage) message).getTimeStamp());
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
                    case "time":
                        LogUtil.log(clockCoordinator.getStatus());
                        break;
                    case "play":
                        clockCoordinator.doNothing();
                        LogUtil.log("playing, local time +1s");
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
