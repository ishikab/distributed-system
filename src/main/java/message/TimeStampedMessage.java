package message;

import clock.ClockCoordinator;
import clock.LogicalTimeStamp;
import clock.TimeStamp;
import clock.VectorTimeStamp;
import logger.LogUtil;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Comparator;

public class TimeStampedMessage extends Message {
    private TimeStamp timeStamp;
    public TimeStampedMessage(String source, BufferedReader br) {
        super(source, br);
        if (ClockCoordinator.getClockType() == ClockCoordinator.ClockType.LOGICAL)
            this.timeStamp = new LogicalTimeStamp();
        else this.timeStamp = new VectorTimeStamp();
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
}
