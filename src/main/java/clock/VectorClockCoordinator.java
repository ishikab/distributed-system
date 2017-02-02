/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock;

import config.Configuration;
import logger.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public class VectorClockCoordinator extends ClockCoordinator {
    private static VectorClockCoordinator instance = new VectorClockCoordinator();

    public static VectorClockCoordinator getInstance() {
        return instance;
    }

    private VectorClockCoordinator() {
        localNodeId = Configuration.nodeMap.get(Configuration.localName).getNodeId();
        VectorTimeStamp.initVectorTimeStamp(localNodeId);
    }

    @Override
    public void doNothing() {
        VectorTimeStamp.incrementTime();
    }

    @Override
    public AtomicInteger getLocalTime() {
        return VectorTimeStamp.getCurrentTimeStamp().get(this.localNodeId);
    }

    @Override
    public ArrayList<AtomicInteger> getStatus() {
        return VectorTimeStamp.getCurrentTimeStamp();
    }

    @Override
    public void updateTime(TimeStamp timeStamp) {
        if (timeStamp instanceof VectorTimeStamp) {
            ArrayList<AtomicInteger> localTime = getStatus();
            ArrayList<AtomicInteger> remoteTime = ((VectorTimeStamp) timeStamp).getValue();
            for (int i = 0; i < localTime.size(); i++) {
                int newTime = Math.max(localTime.get(i).get(), remoteTime.get(i).get()) + 1;
                localTime.get(i).set(newTime);
            }
            localTime.get(localNodeId).addAndGet(1);
            LogUtil.info(String.format("Time change (%s, %s) -> %s", localTime, remoteTime, LogicalTimeStamp.getCurrentTimeStamp().get()));
        } else {
            LogUtil.error("Inconsistent time stamp type, should be VectorTimeStamp");
        }
    }
}
