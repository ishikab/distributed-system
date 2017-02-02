/*
 * 18-842 Distributed Systems Team 6
 * Chenxi Wang (chenxi.wang@sv.cmu.edu)
 * Ishika Batra (ibatra@andrew.cmu.edu)
 */

package org.slf4j.impl2;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Created by wcx73 on 2017/2/1.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
    private static final StaticLoggerBinder singleton = new StaticLoggerBinder();
    private final ILoggerFactory factory = new LoggerFactory();
    @Override
    public ILoggerFactory getLoggerFactory() {
        return factory;
    }
    private final String factoryClassName = this.factory.getClass().getName();

    @Override
    public String getLoggerFactoryClassStr() {
        return this.factoryClassName;
    }
}
