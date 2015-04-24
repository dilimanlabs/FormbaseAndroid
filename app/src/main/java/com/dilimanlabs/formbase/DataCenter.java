package com.dilimanlabs.formbase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 3/3/2015.
 */
public class DataCenter {
    public static boolean hasViews = false;
    public static  String TOKEN = "";
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
