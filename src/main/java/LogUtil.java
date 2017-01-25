/**
 * Created by chenxiw on 1/23/17.
 * chenxi.wang@sv.cmu.edu
 */
public class LogUtil {
    public static void log(String message){
        System.out.println(message);
    }
    public static void log(Object message){
        log(message.toString());
    }
    public static void logErr(String message) {
        System.err.println(message);
    }
    public static void logFatalErr(String message) {
        logErr(message);
        System.exit(-1);
    }
}
