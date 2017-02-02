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

public class VectorClockCoordinator extends ClockCoordinator {
    private static VectorClockCoordinator instance = new VectorClockCoordinator();

    public static VectorClockCoordinator getInstance() {
        return instance;
    }

    private VectorClockCoordinator() {
        LogUtil.debug("Creating Vector Clock Coordinator");
        localNodeId = Configuration.nodeMap.get(Configuration.localName).getNodeId();
        VectorTimeStamp.initVectorTimeStamp(localNodeId);
    }

    @Override
    public void doNothing() {
        VectorTimeStamp.incrementTime();
    }

    @Override
    public AtomicInteger getLocalTime() {
        return VectorTimeStamp.getCurrentTimeStamp().get(localNodeId);
    }

    @Override
    public ArrayList<AtomicInteger> getStatus() {
        return VectorTimeStamp.getCurrentTimeStamp();
    }

    @Override
    public void updateTime(TimeStamp timeStamp) {
        if (timeStamp instanceof VectorTimeStamp) {
            ArrayList<AtomicInteger> localTime = getStatus();
            LogUtil.debug("local:  " + localTime);
            ArrayList<AtomicInteger> remoteTime = ((VectorTimeStamp) timeStamp).getValue();
            LogUtil.debug("remote: " + remoteTime);
            for (int i = 0; i < localTime.size(); i++) {
                int newTime = Math.max(localTime.get(i).get(), remoteTime.get(i).get());
                localTime.get(i).set(newTime);
            }
            localTime.get(localNodeId).addAndGet(1);
            LogUtil.info(String.format("%s", getStatus()));
        } else {
            LogUtil.error("Inconsistent time stamp type, should be VectorTimeStamp");
        }
    }
}
