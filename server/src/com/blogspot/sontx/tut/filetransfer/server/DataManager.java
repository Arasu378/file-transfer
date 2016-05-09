package com.blogspot.sontx.tut.filetransfer.server;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;

import java.io.File;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public final class DataManager {
    private static DataManager instance = new DataManager();

    public static DataManager  getInstance() {
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }

    private SQLiteQueue queue;

    private DataManager() {
        String userDir = System.getProperty("user.dir");
        File dbFile = new File(userDir, "ffdb");
        boolean dbExists = dbFile.exists();
        queue = new SQLiteQueue(dbFile);
        queue.start();
        if (!dbExists)
            createDatabase();
    }

    private void createDatabase() {
        queue.execute(new SQLiteJob<Void>() {
            @Override
            protected Void job(SQLiteConnection connection) throws Throwable {
                String sql = "CREATE  TABLE \"main\".\"accounts\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"username\" VARCHAR NOT NULL  UNIQUE , \"password\" VARCHAR NOT NULL )";
                connection.exec(sql);
                return null;
            }
        }).complete();
    }

    public boolean checkLogin(final String username, final String password) {
        return queue.execute(new SQLiteJob<Boolean>() {
            @Override
            protected Boolean job(SQLiteConnection connection) throws Throwable {
                String sql = String.format("SELECT * FROM accounts WHERE username='%s' AND password='%s'", username, password);
                SQLiteStatement statement = connection.prepare(sql);
                boolean result = statement.step();
                statement.dispose();
                return result;
            }
        }).complete();
    }

    private void stop () {
        queue.stop(true);
    }
}
