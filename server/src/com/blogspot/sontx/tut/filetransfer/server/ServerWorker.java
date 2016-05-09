package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Worker;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public abstract class ServerWorker extends Worker {

    public ServerWorker(Socket socket) throws IOException {
        initializeConnection(socket);
    }

    protected abstract void processData(Data data) throws IOException;

    @Override
    protected void runOnBackground() throws IOException {
        while (true) {
            Data data = readData();
            if (data == null)
                break;
            processData(data);
        }
    }
}
