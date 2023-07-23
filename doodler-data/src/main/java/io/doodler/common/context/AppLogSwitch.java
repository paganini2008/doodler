package io.doodler.common.context;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: AppLogSwitch
 * @Author: Fred Feng
 * @Date: 23/05/2023
 * @Version 1.0.0
 */
public abstract class AppLogSwitch {

    private static final AtomicBoolean local = new AtomicBoolean(false);
    private static final AtomicBoolean feign = new AtomicBoolean(true);

    public static void setLocal(boolean enabled) {
        local.set(enabled);
    }

    public static void setFeign(boolean enabled) {
        feign.set(enabled);
    }

    public static boolean isLocal() {
        return local.get();
    }

    public static boolean isFeign() {
        return feign.get();
    }
}