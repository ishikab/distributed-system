package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

public class Message implements Serializable, Cloneable {
    private String src = null, dest = null, kind = null;
    private Object data = null;
    private Integer seqNum = -1;
    private Boolean isDuplicate = false;

    public Message(String dest, String kind, Object data) {
        this.dest = dest;
        this.kind = kind;
        this.data = data;
    }

    public Message(String source, BufferedReader br) {
        try {
            this.src = source;
            System.out.print("destination: ");
            this.dest = br.readLine();
            System.out.print("kind: ");
            this.kind = br.readLine();
            System.out.print("message: ");
            this.data = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean isDuplicate() {
        return isDuplicate;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "message.Message{" +
                "src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", kind='" + kind + '\'' +
                ", data=" + data +
                ", seqNum=" + seqNum +
                ", isDuplicate=" + isDuplicate +
                '}';
    }

    protected Message clone() {
        Message message = null;
        try {
            message = (Message) super.clone();
            message.isDuplicate = true;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void setSource(String source) {
        this.src = source;
    }

    /**
     * simple wrapper of well-named method
     *
     * @param source
     */
    public void set_source(String source) {
        setSource(source);
    }

    public Integer getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(Integer seqNum) {
        this.seqNum = seqNum;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public void set_seqNum(int sequenceNumber) {
        this.seqNum = sequenceNumber;
    }

    public void set_duplicate(Boolean dupe) {
        this.isDuplicate = dupe;
    }
}
