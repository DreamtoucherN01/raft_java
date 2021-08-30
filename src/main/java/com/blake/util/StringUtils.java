package com.blake.util;

import java.util.Random;

public class StringUtils {

    public static Random random = new Random();

    public static int parseIntFromObject(Object obj) {

        return Integer.valueOf(String.valueOf(obj));
    }

    public static int randomNext(int bound) {

        return random.nextInt(bound);
    }

}
