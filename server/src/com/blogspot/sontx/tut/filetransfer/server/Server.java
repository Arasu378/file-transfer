package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public abstract class Server extends Thread implements Closeable {
    private final ServerSocket serverSocket;

    public Server(String serverAddress, int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort, 100, InetAddress.getByName(serverAddress));
    }

    @Override
    public void run() {
        try {
            waitForConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Server shutdown!");
    }

    protected abstract void onAcceptSocket(Socket socket) throws IOException;

    protected void waitForConnection() throws IOException {
        while (true) {
            Log.i("Server is waiting for connection...");
            Socket socket = serverSocket.accept();
            onAcceptSocket(socket);
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
