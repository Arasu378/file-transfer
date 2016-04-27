package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bo.Worker;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 4/4/2016.
 */
public abstract class ClientWorker extends Worker {
    private String serverAddress;
    private int serverPort;

    public ClientWorker(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    private void connectToServer() throws IOException {
        initializeConnection(new Socket(serverAddress, serverPort));
    }

    @Override
    protected void runOnBackground() throws IOException {
        connectToServer();
    }
}
