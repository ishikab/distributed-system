/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package org.slf4j.impl2;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory implements ILoggerFactory{
    private final ConcurrentHashMap<String, Logger> loggerMap = new ConcurrentHashMap<>();
    @Override
    public Logger getLogger(String name) {
        if(!loggerMap.containsKey(name))
            loggerMap.put(name, new DistributedLogger());
        return loggerMap.get(name);
    }
}
