/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import message.TimeStampedMessage;

import java.io.*;
import java.util.Scanner;

import clock.TimeStamp;

public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);
    private static String fileName;
    private static List<TimeStampedMessage> loggerMsgs = new ArrayList<TimeStampedMessage>();
    private static TimeStampedMessage cachedMsg = null;
    private static int msgNum = 0;
    private static int concurrentMsgNum = 0;

    public Logger getLogger() {
        return logger;
    }

    public static void log(Object message) {
        System.out.println(message.toString());
    }

    public static void info(Object message) {
        logger.info(message.toString());
    }

    public static void debug(Object message) {
        logger.debug(message.toString());
    }

    private static void logWithIndent(Object message) {
        log("  " + message.toString());
    }

    ;

    @SuppressWarnings("unchecked")
    public static void logIterable(String title, Iterable iterable) {
        log(title);
        iterable.forEach(LogUtil::logWithIndent);
    }

    public static void error(Object message) {
        logger.error(message.toString());
    }

    public static void fatalError(Object message) {
        logger.error(message.toString());
        System.exit(Integer.MIN_VALUE);
    }

    public static void setLogFile(String name) {
        fileName = name;
    }

    public static void addMessageToLogger(TimeStampedMessage msg) {
        if (loggerMsgs.isEmpty()) {
            loggerMsgs.add(msg);
        } else {
            int i = 0;
            while (i < loggerMsgs.size()) {
                if ((loggerMsgs.get(i).getTimeStamp()).compareTo(msg.getTimeStamp()) == TimeStamp.comparision.greater) {
                    if (i == 0) {
                      loggerMsgs.add(i-1, msg);
                      return;
                    }
                    loggerMsgs.add(i-1, msg);
                    return;
                }
                i++;
            }
            loggerMsgs.add(msg);
        }
    }

    public static void writeLogger() {
        
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(fileName, true));
            file.write("\nWritting logs\n");
            file.write("\nIf messages are not parallel they appear in increasing order\n\n");

            for (TimeStampedMessage msg : loggerMsgs) {
                if (cachedMsg != null) {
                    TimeStamp.comparision order = msg.getTimeStamp().compareTo(cachedMsg.getTimeStamp());
                    if (order == TimeStamp.comparision.parallel) {
                        concurrentMsgNum = concurrentMsgNum + 1;
                    } else if (order == TimeStamp.comparision.greater) {
                        concurrentMsgNum = 0;
                        msgNum++;
                    }
                }
                String output = "Message :: " + msg;
                for (TimeStampedMessage msg1 : loggerMsgs) {
                    if (!msg.equals(msg1))
                    {
                      TimeStamp.comparision order = msg1.getTimeStamp().compareTo(msg.getTimeStamp());
                      if (order == TimeStamp.comparision.parallel) {
                          output = output + "\nParallel with " + msg1;
                      }
                    }
                }
                output = output + "\n\n";
                file.write(output);
                cachedMsg = msg;
            }
            file.close();
            //loggerMsgs.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String msgOrder(int mNum, int cNum) {
        if (cNum == 0)
            return "Number (" + mNum + ") ";
        return "Number (" + mNum + "[" + cNum + "] ) ";
    }
}
