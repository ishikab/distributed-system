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
public class VectorTimeStamp extends TimeStamp {

    private static Integer nodeId;
    private static final ArrayList<AtomicInteger> currentTimeStamp = new ArrayList<>();
    private ArrayList<AtomicInteger> value = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public VectorTimeStamp() {
        for(AtomicInteger nodeTimeStamp: currentTimeStamp) {
            this.value.add(new AtomicInteger(nodeTimeStamp.intValue()));
        }
        incrementTime();
    }

    public static void initVectorTimeStamp(int id) {
        nodeId = id;
        for (int i = 0; i < Configuration.getNumNodes(); i++)
            currentTimeStamp.add(new AtomicInteger(0));
    }

    public static void incrementTime() {
        currentTimeStamp.get(nodeId).getAndAdd(1);
    }

    public static ArrayList<AtomicInteger> getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    @Override
    public int compareTo(Object anotherVectorTimeStamp) {
        return 0;
    }

    public ArrayList<AtomicInteger> getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "VectorTimeStamp{" + value.toString() + "}";
    }
}
