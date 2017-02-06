package clock;

import logger.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class LogicalTimeStamp extends TimeStamp {
    private static final AtomicInteger currentTimeStamp = new AtomicInteger(0);
    private AtomicInteger value = new AtomicInteger(0);

    public LogicalTimeStamp() {
        this.value.set(currentTimeStamp.getAndAdd(1));
        LogUtil.debug(this);
    }

    public static void setCurrentTimeStamp(int value) {
        currentTimeStamp.set(value);
    }

    public static AtomicInteger getCurrentTimeStamp() {
        return currentTimeStamp;
    }
    @Override
    public comparision compareTo(Object anotherTimeStamp) {
        if (this.value.get() < ((LogicalTimeStamp)anotherTimeStamp).value.get())
            return comparision.lesser;
        if (this.value.get() > ((LogicalTimeStamp)anotherTimeStamp).value.get())
            return comparision.greater;
        return comparision.parallel;
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
