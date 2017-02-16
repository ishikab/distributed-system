package message;

import java.io.BufferedReader;

public class GroupMessage extends TimeStampedMessage {
    private String groupName;

    public GroupMessage(String grpName, String kind, Object data) {
        super(null, kind, data);
        this.groupName = grpName;
    }	
	
    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public String toString() {
        return "message.GroupMessage{" +
                "src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", kind='" + kind + '\'' +
                ", data=" + data +
                ", seqNum=" + seqNum +
                ", isDuplicate=" + isDuplicate +
                ", timeStamp=" + timeStamp +
                ", groupName=" + groupName +
                '}';
    }
}
