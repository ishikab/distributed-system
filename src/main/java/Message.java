import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by chenxiw on 1/23/17.
 * chenxi.wang@sv.cmu.edu
 */
public class Message implements Serializable {
    String src = null, dest = null, kind = null, data = null;
    public Message(String dest, String kind, Object data) {

    }

    @Override
    public String toString() {
        return "Message{" +
                "dest='" + dest + '\'' +
                ", kind='" + kind + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public Message(BufferedReader br) {
        try {
            System.out.print("destination: ");
            this.dest = br.readLine();
            System.out.print("kind: ");
            this.kind = br.readLine();
            System.out.print("message: ");
            this.data = br.readLine();
            System.out.println(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // These settors are used by MessagePasser.send( ), not your app
    public void set_source(String source){
        this.src = source;
    }
    public void set_seqNum(int sequenceNumber){


    }
    public void set_duplicate(Boolean dupe){

    }
    // other accessors, toString, etc as needed
}