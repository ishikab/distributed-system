/*
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */

public class LogUtil {
    public static void log(String message){
        System.out.println("[INFO] " + message);
    }
    public static void log(Object message){
        log(message.toString());
    }
    public static void logErr(String message) {
        System.err.println("[ERROR] " + message);
    }
    public static void logFatalErr(String message) {
        logErr(message);
        System.exit(-1);
    }
}
