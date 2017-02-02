package clock;

import java.util.concurrent.atomic.AtomicInteger;

public class LogicalTimeStamp extends TimeStamp implements Comparable<LogicalTimeStamp>{
    private static final AtomicInteger timeStampGenerator = new AtomicInteger(0);
    private int value;

    public LogicalTimeStamp() {
        this.value = timeStampGenerator.getAndAdd(1);
    }
    @Override
    public int compareTo(LogicalTimeStamp anotherTimeStamp) {
        return this.value - anotherTimeStamp.value;
    }

    @Override
    public String toString() {
        return "LogicalTimeStamp{" +
                "value=" + value +
                '}';
    }
}
