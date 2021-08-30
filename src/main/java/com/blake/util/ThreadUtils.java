package com.blake.util;

public class ThreadUtils {

    public static void sleep(int time) {

        try {
            Thread.sleep(time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sleepMillis(int time) {

        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
