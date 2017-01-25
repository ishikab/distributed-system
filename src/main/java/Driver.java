import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenxiw on 1/23/17.
 * chenxi.wang@sv.cmu.edu
 */
public class Driver {
    public static void main(String[] args) {
        System.out.println("Welcome to 18-842 Distributed Systems lab 0");
        System.out.println(String.format("local_name: %s\nconfig_file: %s", args[1], args[0]));
        MessagePasser messagePasser = new MessagePasser(args[0], args[1]);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                Message msg;
                System.out.print("Please input [send/receive/exit]: ");
                switch (br.readLine()) {
                    case "send":
                        msg = new Message(br);
                        messagePasser.send(msg);
                        break;
                    case "receive":
                        msg = messagePasser.receive();
                        LogUtil.log(msg);
                        break;
                    case "exit":
                        LogUtil.log("Hmm, okay, exiting...");
                        System.exit(0);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


}
