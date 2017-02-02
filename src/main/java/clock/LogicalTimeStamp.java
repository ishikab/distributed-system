package clock;

import logger.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class LogicalTimeStamp extends TimeStamp implements Comparable<LogicalTimeStamp>{
    private static final AtomicInteger currentTimeStamp = new AtomicInteger(0);
    private AtomicInteger value = new AtomicInteger(0);

    public LogicalTimeStamp() {
        this.value.set(currentTimeStamp.getAndAdd(1));
//        this.value.set(0);
        LogUtil.debug(this);
    }

    public static void setCurrentTimeStamp(int value) {
        currentTimeStamp.set(value);
    }

    public static AtomicInteger getCurrentTimeStamp() {
        return currentTimeStamp;
    }
    @Override
    public int compareTo(LogicalTimeStamp anotherTimeStamp) {
        return this.value.get() - anotherTimeStamp.value.get();
    }

    public void setValue(int val) {
        this.value.set(val);
    }

    public int getValue() {
        return this.value.get();
    }

    @Override
    public String toString() {
        return "LogicalTimeStamp{" +
                "value=" + value +
                '}';
    }

    public static void incrementTime() {
        currentTimeStamp.addAndGet(1);
    }

}
