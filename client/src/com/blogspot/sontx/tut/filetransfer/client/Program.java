package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bo.Log;
import com.blogspot.sontx.tut.filetransfer.bo.Worker;
import com.blogspot.sontx.tut.filetransfer.client.ui.ConnectionWindow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class Program {
    private static Program instance = null;

    public static Program getInstance() {
        if (instance == null)
            instance = new Program();
        return instance;
    }

    private Client client;

    public void setupClient(String address, int port) {
        client = new Client(address, port);
    }

    public Client getClient() {
        return client;
    }

    private Program() {
    }

    public static void main(String[] args) {
        ConnectionWindow connectionWindow = new ConnectionWindow();
        connectionWindow.showWindow();
    }
}
