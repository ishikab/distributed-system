package clock;

import config.Configuration;

import java.io.Serializable;

/**
 * Created by wcx73 on 2017/2/1.
 */
public abstract class TimeStamp implements Serializable, Cloneable, Comparable<Object>{
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
