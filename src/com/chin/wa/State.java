/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chin.wa;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Chin
 */
public class State {

    private static AtomicBoolean shutdown = new AtomicBoolean(false);
    private static AtomicBoolean reloadConfig = new AtomicBoolean(false);
    private static AtomicInteger maxStorage = new AtomicInteger(0);
    private static AtomicInteger nextStorage = new AtomicInteger(0);
    private static AtomicLong lastReloadConfig = new AtomicLong(0);
//    private static final String configFile = "D:\\Working\\Java\\csr\\src\\csr.properties";
    private static final String configFile = "wa.properties";

    public static boolean isShutdown() {
        return shutdown.get();
    }

    public static void setShutdown() {
        shutdown.set(true);
    }

    public static boolean isReloadConfig() {
        return reloadConfig.get();
    }

    public static void setReloadConfig() {
        reloadConfig.set(true);
    }

    public static void setMaxStorage(int max) {
        maxStorage.set(max);
    }

    public static int getNextStorage() {
        if (nextStorage.get()==maxStorage.get()) {
            nextStorage.set(0);
        }
        return nextStorage.getAndIncrement();
    }

    public static long getLastReloadConfig() {
        return lastReloadConfig.get();
    }

    public static void setLastReloadConfig(long next) {
        lastReloadConfig.set(next);
    }

    public static String getConfigFile() {
        return configFile;
    }

}
