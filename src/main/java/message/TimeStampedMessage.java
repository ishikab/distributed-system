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
        LogUtil.info(this.timeStamp);
        LogicalTimeStamp a = new LogicalTimeStamp();
        LogUtil.info(a);

    }

    @Override
    public int compareTo(TimeStampedMessage message) {
        return 0;
    }
}
