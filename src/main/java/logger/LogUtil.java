/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package logger;

import java.util.MissingResourceException;
import java.util.logging.Logger;

public class LogUtil extends Logger{

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    public LogUtil(String name, String resourceBundleName) throws MissingResourceException {
        super(name, resourceBundleName);
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public static void log(Object message) {
        log(message.toString());
    }

    public static void logInfo(Object message) {
        System.out.println("[INFO] " + message.toString());
    }

    private static void logWithIndent(Object message) {log("  " + message.toString());};

    @SuppressWarnings("unchecked")
    public static void logIterable(String title, Iterable iterable) {
        log(title);
        iterable.forEach(LogUtil::logWithIndent);
    }

    public static void logErr(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void logFatalErr(String message) {
        logErr(message);
        System.exit(-1);
    }
}
