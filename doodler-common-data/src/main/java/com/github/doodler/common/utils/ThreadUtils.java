package com.github.doodler.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomUtils;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: ThreadUtils
 * @Author: Fred Feng
 * @Date: 27/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class ThreadUtils {

    public static boolean randomSleep(long to) {
        return randomSleep(0, to);
    }

    public static boolean randomSleep(long from, long to) {
        return sleep(RandomUtils.nextLong(from, to));
    }

    public static boolean sleep(long timeout, TimeUnit timeUnit) {
        return sleep(DateUtils.convertToMillis(timeout, timeUnit));
    }

    public static boolean sleep(long ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignored) {
                return false;
            }
        }
        return true;
    }

    public static Thread runAsThread(Runnable runnable) {
        return runAsThread(runnable, null);
    }

    public static Thread runAsThread(Runnable runnable,
            Thread.UncaughtExceptionHandler exceptionHandler) {
        Thread t = new Thread(runnable);
        if (exceptionHandler != null) {
            t.setUncaughtExceptionHandler(exceptionHandler);
        }
        t.start();
        return t;
    }

    public static Thread runAsThread(String name, Runnable runnable) {
        return runAsThread(name, runnable, null);
    }

    public static Thread runAsThread(String name, Runnable runnable,
            Thread.UncaughtExceptionHandler exceptionHandler) {
        Thread t = new Thread(runnable, name);
        if (exceptionHandler != null) {
            t.setUncaughtExceptionHandler(exceptionHandler);
        }
        t.start();
        return t;
    }

    public static Thread runAsThread(ThreadFactory threadFactory, Runnable runnable) {
        Thread t = threadFactory.newThread(runnable);
        t.start();
        return t;
    }

    public static String currentThreadName() {
        return Thread.currentThread().getName();
    }

}
