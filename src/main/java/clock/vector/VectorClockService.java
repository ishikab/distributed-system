/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock.vector;

import clock.ClockService;
import clock.TimeStamp;
import config.Configuration;
import logger.LogUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class VectorClockService extends ClockService {
    private static VectorClockService instance = new VectorClockService();

    private VectorClockService() {
        LogUtil.debug("Creating Vector Clock Coordinator");
        localNodeId = Configuration.nodeMap.get(Configuration.localName).getNodeId();
        VectorTimeStamp.initVectorTimeStamp(localNodeId);
    }

    public static VectorClockService getInstance() {
        return instance;
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
            ArrayList<AtomicInteger> remoteTime = ((VectorTimeStamp) timeStamp).getValue();
            for (int i = 0; i < localTime.size(); i++) {
                int newTime = Math.max(localTime.get(i).get(), remoteTime.get(i).get());
                localTime.get(i).set(newTime);
            }
            localTime.get(localNodeId).addAndGet(1);
        } else {
            LogUtil.error("Inconsistent time stamp type, should be VectorTimeStamp");
        }
    }
}
