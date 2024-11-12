package com.github.doodler.common.context;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: ApiDebuger
 * @Author: Fred Feng
 * @Date: 23/05/2023
 * @Version 1.0.0
 */
public abstract class ApiDebuger {

    private static final AtomicBoolean serverSide = new AtomicBoolean(false);
    private static final AtomicBoolean restClient = new AtomicBoolean(true);

    public static void enableServerSide(boolean enabled) {
    	serverSide.set(enabled);
    }

    public static void enableRestClient(boolean enabled) {
    	restClient.set(enabled);
    }

    public static boolean enableServerSide() {
        return serverSide.get();
    }

    public static boolean enableRestClient() {
        return restClient.get();
    }
}