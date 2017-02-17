package message;

import multicast.MulticastCoordinator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupMessage extends TimeStampedMessage {
    private String groupName = null;
    private ConcurrentHashMap<String, AtomicInteger> groupTimeStamp = null;

    public GroupMessage(String src, String groupName, String kind, Object data) {
        super(src, null, kind, data);
        this.groupName = groupName;
    }
    public ConcurrentHashMap<String, AtomicInteger> getGroupTimeStamp() {
        return this.groupTimeStamp;
    }

    public GroupMessage(GroupMessage message) {
        this.src = message.src;
        this.dest = message.dest;
        this.kind = message.kind;
        this.seqNum = message.seqNum;
        this.isDuplicate = message.isDuplicate;
        this.data = message.data;
        this.timeStamp = message.timeStamp;
        this.groupName = message.groupName;
    }
    public void setGroupTimeStamp(ConcurrentHashMap<String, AtomicInteger> hashMap) {
        this.groupTimeStamp = hashMap;
    }

    public void setCurrentGroupTimeStamp(String groupName) {
        this.groupTimeStamp = MulticastCoordinator.getGroupTimeStampCopy(groupName);
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
                ", groupTimeStamp=" + groupTimeStamp +
                '}';
    }
}
