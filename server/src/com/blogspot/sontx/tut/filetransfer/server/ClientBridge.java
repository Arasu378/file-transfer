package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Data;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class ClientBridge extends ServerWorker {

    public ClientBridge(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    protected void processData(Data data) throws IOException {

    }
}
