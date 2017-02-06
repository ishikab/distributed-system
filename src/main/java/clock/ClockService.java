/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock;


import clock.logical.LogicalClockService;
import clock.vector.VectorClockService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public abstract class ClockService {
    protected static Integer localNodeId;
    private static ClockType clockType = null;

    public static ClockType getClockType() {
        return clockType;
    }

    public static void setClockType(ClockType clockType) {
        ClockService.clockType = clockType;
    }

    public static ClockService getInstance() {
        switch (clockType) {
            case VECTOR:
                return VectorClockService.getInstance();
            case LOGICAL:
            default:
                return LogicalClockService.getInstance();
        }
    }

    public abstract void doNothing();

    public abstract AtomicInteger getLocalTime();

    public abstract Object getStatus();

    public abstract void updateTime(TimeStamp timeStamp);

    public enum ClockType {LOGICAL, VECTOR}

}
