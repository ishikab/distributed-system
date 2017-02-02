package message;

import clock.LogicalTimeStamp;
import clock.TimeStamp;
import logger.LogUtil;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by wcx73 on 2017/2/1.
 */
public class TimeStampedMessage extends Message implements Comparable<TimeStampedMessage> {
    private TimeStamp timeStamp;
    public TimeStampedMessage(String source, BufferedReader br) {
        super(source, br);
        this.timeStamp = new LogicalTimeStamp();
    }

    @Override
    public String toString() {
        return "message.TimeStampedMessage{" +
                "src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", kind='" + kind + '\'' +
                ", data=" + data +
                ", seqNum=" + seqNum +
                ", isDuplicate=" + isDuplicate +
                ", timeStamp=" + timeStamp +
                '}';
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int compareTo(TimeStampedMessage message) {
        return 0;
    }

}
