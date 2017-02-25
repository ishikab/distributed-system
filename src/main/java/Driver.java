import clock.ClockService;
import logger.LogUtil;
import message.GroupMessage;
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
        LogUtil.println("Welcome to 18-842 Distributed Systems lab1 project");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String localName, configFileName, mode;
        if (args.length < 3) {
            System.out.print("Please enter local name:: ");
            localName = br.readLine();
            System.out.print("Please enter the name of configuration file:: ");
            configFileName = br.readLine();
            if (configFileName.equals("")) {
                LogUtil.info("using default config file ./config.yaml");
                configFileName = "config.yaml";
            }
            System.out.print("Please enter clock type [logical/vector]:: ");
            mode = br.readLine();
        } else {
            localName = args[1];
            configFileName = args[0];
            mode = args[2];
            LogUtil.info("Reading from command line args");
        }
        if (mode.toLowerCase().equals("logical"))
            ClockService.setClockType(ClockService.ClockType.LOGICAL);
        else if (mode.toLowerCase().equals("vector"))
            ClockService.setClockType(ClockService.ClockType.VECTOR);
        else {
            LogUtil.println("Invalid clock type, setting default vector");
        }

        MessagePasser messagePasser = new MessagePasser(configFileName, localName);

        ClockService clockService = ClockService.getInstance();

        Message message;
        String data, kind, groupName, dest;
        while (true) {
            try {
                LogUtil.print("send/receive/multicast/exit/rules/nodes/time >>> ");
                switch (br.readLine()) {
                    case "request":
                        messagePasser.requestResource();
                        break;
                    case "release":
                        messagePasser.releaseRecourse();
                        break;
                    case "multicast":
                        LogUtil.print("group: ");
                        groupName = br.readLine();
                        LogUtil.print("kind: ");
                        kind = br.readLine();
                        LogUtil.print("message: ");
                        data = br.readLine();
                        messagePasser.multicast(new GroupMessage(localName, groupName, kind, data));
                        break;
                    case "send":
                        LogUtil.print("destination: ");
                        dest = br.readLine();
                        LogUtil.print("kind: ");
                        kind = br.readLine();
                        LogUtil.print("message: ");
                        data = br.readLine();
                        messagePasser.send(new TimeStampedMessage(localName, dest, kind, data));
                        break;
                    case "receive":
                        message = messagePasser.receive();
                        //if (message instanceof TimeStampedMessage)
                        //    iclockCoordinator.updateTime(((TimeStampedMessage) message).getTimeStamp());
                        if (message == null) LogUtil.println("No new message");
                        else LogUtil.println(message);
                        break;

                    case "exit":
                        LogUtil.println("Thanks for using");
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
                        LogUtil.println(clockService.getStatus());
                        break;
                    case "groups":
                        messagePasser.listGroups();
                        break;
                    case "play":
                        clockService.doNothing();
                        LogUtil.println("playing, local time +1s");
                        break;
                    default:
                        LogUtil.println("available commands: send/receive/multicast/exit/rules/nodes/time");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
