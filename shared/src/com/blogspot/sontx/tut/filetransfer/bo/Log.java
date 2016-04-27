package com.blogspot.sontx.tut.filetransfer.bo;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 7/4/2016.
 */
public final class Log {
    Log() {}

    private static void v(String tag, Object obj) {
        System.out.println(String.format("[%s] %s", tag, obj));
    }

    public static void i(Object obj) {
        v("INF", obj);
    }

    public static void e(Object obj) {
        v("ERR", obj);
    }
}
