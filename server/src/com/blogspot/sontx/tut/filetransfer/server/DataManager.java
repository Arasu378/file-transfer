package com.blogspot.sontx.tut.filetransfer.server;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public final class DataManager {
    private static DataManager instance = new DataManager();

    public static DataManager  getInstance() {
        return instance;
    }

    private DataManager() {}

    public boolean checkLogin(String username, String password) {
        return username.equals("sontx") && password.equals("123");
    }
}
