/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock;


import java.util.concurrent.atomic.AtomicInteger;

import static clock.ClockCoordinator.ClockType.LOGICAL;
import static clock.ClockCoordinator.ClockType.VECTOR;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public abstract class ClockCoordinator {
    public enum ClockType {LOGICAL, VECTOR}
    static Integer localNodeId;
    static ClockType clockType = VECTOR;

    public static ClockType getClockType() {
        return clockType;
    }

    public static void setClockType(ClockType clockType) {
        ClockCoordinator.clockType = clockType;
    }

    public static ClockCoordinator getInstance() {
        switch (clockType) {
            case VECTOR:
                return VectorClockCoordinator.getInstance();
            case LOGICAL:
            default:
                return LogicalClockCoordinator.getInstance();
        }
    }

    public abstract void doNothing();

    public abstract AtomicInteger getLocalTime();

    public abstract Object getStatus();

    public abstract void updateTime(TimeStamp timeStamp);

}
