package message;

import clock.ClockService;
import clock.TimeStamp;
import clock.logical.LogicalTimeStamp;
import clock.vector.VectorTimeStamp;

import java.io.BufferedReader;

public class TimeStampedMessage extends Message {
    private TimeStamp timeStamp;

    public TimeStampedMessage(String source, BufferedReader br) {
        super(source, br);
        if (ClockService.getClockType() == ClockService.ClockType.LOGICAL)
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
