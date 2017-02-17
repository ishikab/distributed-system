package message;

import clock.ClockService;
import clock.TimeStamp;
import clock.logical.LogicalTimeStamp;
import clock.vector.VectorTimeStamp;

import java.io.BufferedReader;

public class TimeStampedMessage extends Message {
    protected TimeStamp timeStamp;

    @Deprecated
    public TimeStampedMessage(String source, BufferedReader br) {
        super(source, br);
        if (ClockService.getClockType() == ClockService.ClockType.LOGICAL)
            this.timeStamp = new LogicalTimeStamp();
        else this.timeStamp = new VectorTimeStamp();
    }
   
    public TimeStampedMessage(String src, String dest, String kind, Object data) {
       super(src, dest, kind, data);
       if (ClockService.getClockType() == ClockService.ClockType.LOGICAL)
            this.timeStamp = new LogicalTimeStamp();
        else this.timeStamp = new VectorTimeStamp();
    }

    public TimeStampedMessage(){
        super();
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
    public void setTimeStamp(TimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
