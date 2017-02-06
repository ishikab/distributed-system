import clock.ClockService;
import logger.LogUtil;
import message.MessagePasser;
import message.TimeStampedMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        LogUtil.println("Welcome to 18-842 Distributed Systems lab1 logger");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String localName, configFileName, mode;
        if (args.length < 3) {
            LogUtil.print("Please enter logger node name:: ");
            localName = br.readLine();
            LogUtil.print("Please enter the name of configuration file:: ");
            configFileName = br.readLine();
            if (configFileName.equals("")) {
                LogUtil.info("using default config file ./config.yaml");
                configFileName = "config.yaml";
            }
            LogUtil.print("Please enter clock type [logical/vector]:: ");
            mode = br.readLine();
        } else {
            localName = args[1];
            configFileName = args[0];
            mode = args[2];
            LogUtil.info("Reading from CLI");
        }
        switch (mode.toLowerCase()) {
            case "logical":
                ClockService.setClockType(ClockService.ClockType.LOGICAL);
                break;
            case "vector":
                ClockService.setClockType(ClockService.ClockType.VECTOR);
                break;
            default:
                LogUtil.error("Unsupported Clock Type: " + mode);
        }

        MessagePasser messagePasser = new MessagePasser(configFileName, localName);
//        ClockService clockCoordinator = ClockService.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH_mm");
        new File("println").mkdir();
//        LogUtil.setLogFile("println/logfile_" + sdfDate.format(new Date()));
        Thread t = new Thread(() -> {
            while (true) {
                TimeStampedMessage message = (TimeStampedMessage) (messagePasser.receive());
                if (message != null) LogUtil.addMessageToLogger(message);
            }
        });
        t.start();
        boolean logToStdOut = true;
        LogUtil.info("Logging target: STDOUT");
        while (true) {
            try {
                LogUtil.print("Please enter [switch/print]>>> ");
                switch (br.readLine().trim().toLowerCase()) {
                    case "switch":
                        logToStdOut = !logToStdOut;
                        LogUtil.info("Now Logging to:" + (logToStdOut ? "STDOUT" : "FILE"));
                        break;
                    case "print":
                        LogUtil.writeLogger(logToStdOut);
                        break;
                    default:
//                        TimeUnit.MINUTES.sleep(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
