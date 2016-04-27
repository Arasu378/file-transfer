package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public class ServerHandler extends Server {
    private static ServerHandler instance = null;
    public static ServerHandler getCurrentInstance() {
        return instance;
    }

    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public ServerHandler(String serverAddress, int serverPort) throws IOException {
        super(serverAddress, serverPort);
        Log.i("ServerHandler is running...");
        instance = this;
    }

    @Override
    protected void onAcceptSocket(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket);
        synchronized (instance) {
            clientHandlers.add(clientHandler);
        }
        clientHandler.start();
        Log.i(String.format("Client joined: %s", socket.getRemoteSocketAddress()));
    }

    @Override
    public void close() throws IOException {
        super.close();
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.close();
        }
    }
}
