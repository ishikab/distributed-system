/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public abstract class ClockCoordinator {
    public enum ClockType {LOGICAL, VECTOR}

    static Integer localNodeId;
    static TimeStamp localTimeStamp;

    public static ClockCoordinator getInstance(ClockType clockType) {
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
