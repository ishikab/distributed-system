package org.slf4j.impl;

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
