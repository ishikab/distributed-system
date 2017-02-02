package clock;

import java.io.Serializable;

/**
 * Created by wcx73 on 2017/2/1.
 */
public abstract class TimeStamp implements Serializable, Cloneable{
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
