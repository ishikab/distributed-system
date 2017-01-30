/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

class LogUtil {
    static void log(String message) {
        System.out.println(message);
    }

    static void log(Object message) {
        log(message.toString());
    }

    static void logInfo(Object message) {
        System.out.println("[INFO] " + message.toString());
    }

    @SuppressWarnings("unchecked")
    static void logIterable(String title, Iterable iterable) {
        log(title.toUpperCase());
        iterable.forEach(LogUtil::log);
        log("");
    }

    static void logErr(String message) {
        System.err.println("[ERROR] " + message);
    }

    static void logFatalErr(String message) {
        logErr(message);
        System.exit(-1);
    }
}
