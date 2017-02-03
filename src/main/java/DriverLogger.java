import clock.ClockCoordinator;
import clock.LogicalTimeStamp;
import logger.LogUtil;
import message.Message;
import message.MessagePasser;
import message.TimeStampedMessage;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;
/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */


/**
 * Created by ibatra on 2/3/17.
 */

/**
 * user interface for demo
 */
public class DriverLogger {
    public static void main(String[] args) throws IOException, InterruptedException {
        LogUtil.log("Welcome to 18-842 Distributed Systems lab project logger");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String localName, configFileName, mode;
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
            mode = br.readLine();
        } else {
            localName = args[1];
            configFileName = args[0];
            mode = args[2];
            LogUtil.info("Reading from command line args");
        }
        if(mode.toLowerCase().equals("logical"))
            ClockCoordinator.setClockType(ClockCoordinator.ClockType.LOGICAL);
        else if(mode.toLowerCase().equals("vector"))
            ClockCoordinator.setClockType(ClockCoordinator.ClockType.VECTOR);
        else {
            LogUtil.log("Invalid clock type, setting default vector");
        }

        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
        ClockCoordinator clockCoordinator = ClockCoordinator.getInstance();
        LogUtil.setLogFile("log");
        Thread t = new Thread() {
            public void run() {
        while (true) {
                TimeStampedMessage message;
                message = (TimeStampedMessage)(messagePasser.receive());
                if (message != null) {
                  if (message instanceof TimeStampedMessage)
                  clockCoordinator.updateTime(((TimeStampedMessage) message).getTimeStamp());
                  LogUtil.log(message);
                  LogUtil.addMessageToLogger(message); 
               }
        }
     }
    };
    t.start();
    while(true){
            try {
                System.out.print("Do you want to write log?::");
                switch (br.readLine()) {
                    case "yes":
                        System.out.println("Logger writing logs");
                        LogUtil.writeLogger();
                    default:
                        TimeUnit.MINUTES.sleep(1);
                        
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        	System.out.println("Logger writing logs");
        	LogUtil.writeLogger();
        }

    }
}
