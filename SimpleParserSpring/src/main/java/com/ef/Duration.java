package com.ef;

public enum Duration {
    hourly, daily;

    public static long getTimeMillis(Duration d) {
        long t = 0;
        if (d.equals(Duration.hourly)) {
            t = 1000 * 60 * 60;
        } else if (d.equals(Duration.daily)) {
            t = 1000 * 60 * 60 * 24;
        }
        return t;
    }
}
