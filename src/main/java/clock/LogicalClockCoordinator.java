/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock;

import logger.LogUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public class LogicalClockCoordinator extends ClockCoordinator {
    private static LogicalClockCoordinator instance = new LogicalClockCoordinator();
    public static LogicalClockCoordinator getInstance() {
        return instance;
    }

    private LogicalClockCoordinator() {
//        this.timeStamp = new LogicalTimeStamp();
        LogUtil.info("Creating Logical Clock Coordinator");
    }

    @Override
    public void doNothing() {
        LogicalTimeStamp.incrementTime();
    }

    @Override
    public AtomicInteger getLocalTime() {
        return LogicalTimeStamp.getCurrentTimeStamp();
    }

    @Override
    public Integer getStatus() {
        return LogicalTimeStamp.getCurrentTimeStamp().get();
    }

    @Override
    public void updateTime(TimeStamp timeStamp) {
        if (timeStamp instanceof LogicalTimeStamp) {
            int localTime = getLocalTime().get();
            int remoteTime = ((LogicalTimeStamp) timeStamp).getValue();
            int newTime = Math.max(localTime, remoteTime) + 1;
            LogicalTimeStamp.setCurrentTimeStamp(newTime);
            LogUtil.info(String.format("Time change (%d, %d) -> %d", localTime, remoteTime, LogicalTimeStamp.getCurrentTimeStamp().get()));
        } else {
            LogUtil.error("Inconsistent time stamp type");
        }
    }


}
