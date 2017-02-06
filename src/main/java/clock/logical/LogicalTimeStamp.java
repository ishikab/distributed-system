package clock.logical;

import clock.TimeStamp;
import logger.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class LogicalTimeStamp extends TimeStamp {
    private static final AtomicInteger currentTimeStamp = new AtomicInteger(0);
    private AtomicInteger value = new AtomicInteger(0);

    public LogicalTimeStamp() {
        currentTimeStamp.getAndAdd(1);
        this.value.set(currentTimeStamp.get());
        LogUtil.debug(this);
    }

    public static AtomicInteger getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    public static void setCurrentTimeStamp(int value) {
        currentTimeStamp.set(value);
    }

    public static void incrementTime() {
        currentTimeStamp.addAndGet(1);
    }

    @Override
    public Comparision compareTo(Object anotherTimeStamp) {
        if (this.value.get() < ((LogicalTimeStamp) anotherTimeStamp).value.get())
            return Comparision.lesser;
        if (this.value.get() > ((LogicalTimeStamp) anotherTimeStamp).value.get())
            return Comparision.greater;
        return Comparision.parallel;
    }

    public int getValue() {
        return this.value.get();
    }

    public void setValue(int val) {
        this.value.set(val);
    }

    @Override
    public String toString() {
        return "LogicalTimeStamp{" +
                "value=" + value +
                '}';
    }

}
