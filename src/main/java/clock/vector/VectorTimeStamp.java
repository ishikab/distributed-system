/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package clock.vector;

import clock.TimeStamp;
import config.Configuration;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenxiw on 2/2/17.
 * chenxi.wang@sv.cmu.edu
 */
public class VectorTimeStamp extends TimeStamp {
    private static final ArrayList<AtomicInteger> currentTimeStamp = new ArrayList<>();
    private static Integer nodeId = null;
    private ArrayList<AtomicInteger> value = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public VectorTimeStamp() {
        incrementTime();
        for (AtomicInteger nodeTimeStamp : currentTimeStamp) {
            this.value.add(new AtomicInteger(nodeTimeStamp.intValue()));
        }
    }

    static void initVectorTimeStamp(int id) {
        nodeId = id;
        for (int i = 0; i < Configuration.getNumNodes(); i++)
            currentTimeStamp.add(new AtomicInteger(0));
    }

    static void incrementTime() {
        currentTimeStamp.get(nodeId).getAndAdd(1);
    }

    static ArrayList<AtomicInteger> getCurrentTimeStamp() {
        return currentTimeStamp;
    }

    @Override
    public Comparision compareTo(Object anotherVectorTimeStamp) {
        Boolean equal = true;
        Boolean less = true;
        Boolean greater = true;
        for (int i = 0; i < this.value.size(); i++) {
            if (this.value.get(i).intValue() > ((VectorTimeStamp) anotherVectorTimeStamp).value.get(i).intValue()) {
                less = false;
                equal = false;
            }
            if (this.value.get(i).intValue() < ((VectorTimeStamp) anotherVectorTimeStamp).value.get(i).intValue()) {
                greater = false;
                equal = false;
            }
        }
        if (less)
            return Comparision.lesser;
        if (greater)
            return Comparision.greater;
        if (equal)
            return Comparision.equal;
        return Comparision.parallel;
    }

    public ArrayList<AtomicInteger> getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "VectorTimeStamp{" + value.toString() + "}";
    }
}
