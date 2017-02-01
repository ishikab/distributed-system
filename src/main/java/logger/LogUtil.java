/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package logger;

public class LogUtil {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(Object message) {
        log(message.toString());
    }

    public static void logInfo(Object message) {
        System.out.println("[INFO] " + message.toString());
    }

    @SuppressWarnings("unchecked")
    public static void logIterable(String title, Iterable iterable) {
        log(title.toUpperCase());
        iterable.forEach(LogUtil::log);
        log("");
    }

    public static void logErr(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void logFatalErr(String message) {
        logErr(message);
        System.exit(-1);
    }
}
