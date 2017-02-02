/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public Logger getLogger(){
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

    private static void logWithIndent(Object message) {log("  " + message.toString());};

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
}
