/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package logger;

import clock.TimeStamp;
import message.TimeStampedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogUtil {
    private static final Logger stdLogger = LoggerFactory.getLogger(LogUtil.class);
    private static final Logger eventLogger = LoggerFactory.getLogger("events");
    private static String logFileName = null;
    private static List<TimeStampedMessage> loggerMessages = new ArrayList<TimeStampedMessage>();

    public static void println(Object message) {
        System.out.println(message.toString());
    }

    public static void print(Object message) {
        System.out.print(message.toString());
    }

    public static void info(Object message) {
        stdLogger.info(message.toString());
    }

    public static void debug(Object message) {
        stdLogger.debug(message.toString());
    }

    private static void logWithIndent(Object message) {
        println("  " + message.toString());
    }

    @SuppressWarnings("unchecked")
    public static void logIterable(String title, Iterable iterable) {
        println(title);
        iterable.forEach(LogUtil::logWithIndent);
    }

    public static void error(Object message) {
        stdLogger.error(message.toString());
    }

    public static void fatalError(Object message) {
        stdLogger.error(message.toString());
        System.exit(Integer.MIN_VALUE);
    }

    public static void setLogFile(String name) {
        logFileName = name;
    }

    public static void addMessageToLogger(TimeStampedMessage message) {
        if (loggerMessages.isEmpty()) {
            loggerMessages.add(message);
        } else {
            int i = 0;
            while (i < loggerMessages.size()) {
                if ((loggerMessages.get(i).getTimeStamp()).compareTo(message.getTimeStamp()) == TimeStamp.Comparision.greater) {
                    if (i == 0) {
                        loggerMessages.add(0, message);
                        return;
                    }
                    loggerMessages.add(i - 1, message);
                    return;
                }
                i++;
            }
            loggerMessages.add(message);
        }
    }

    public static void writeLogger(boolean toStdOut) {
        Logger logger = null;
        if (toStdOut) {
            logger = stdLogger;
        } else {
            logger = eventLogger;
        }
        for (TimeStampedMessage msg : loggerMessages) {
            String output = "Message :: " + msg;
            for (TimeStampedMessage msg1 : loggerMessages) {
                if (!msg.equals(msg1)) {
                    TimeStamp.Comparision order = msg1.getTimeStamp().compareTo(msg.getTimeStamp());
                    if (order == TimeStamp.Comparision.parallel) {
                        output = output + "\nParallel with " + msg1;
                    }
                    else if (order == TimeStamp.Comparision.greater) {
                        output = output + "\nHappens before " + msg1;
                    }
                    else if (order == TimeStamp.Comparision.lesser) {
                        output = output + "\nHappens after " + msg1;
                    }
                }
            }
            output = output + "\n\n";
            logger.info(output);
        }

    }

    public Logger getLogger() {
        return stdLogger;
    }
}
