package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Account;
import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bean.FileHeader;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public class ServerHandler extends Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public ServerHandler(String serverAddress, int serverPort) throws IOException {
        super(serverAddress, serverPort);
        Log.i("ServerHandler is running...");
    }

    @Override
    protected void onAcceptSocket(Socket socket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket);
        clientHandler.start();
        Log.i(String.format("Client joined: %s", socket.getRemoteSocketAddress()));
    }

    private void addClient(ClientHandler which) throws IOException {
        synchronized (this) {
            broadcastClientAttached(which);
            clientHandlers.add(which);
            Log.i("Added client handler");
        }
    }

    private void removeClient(ClientHandler which) throws IOException {
        synchronized (this) {
            clientHandlers.remove(which);
            broadcastClientDetached(which);
            Log.i("Removed client handler");
        }
    }

    private void broadcastClientAttached(ClientHandler which) throws IOException {
        String username = String.format("%d;%s", Data.TYPE_CMD_FRIEND_ADDED, which.username);
        for (ClientHandler clientHandler :  clientHandlers) {
            clientHandler.writeData(new Data(Data.TYPE_CMD_FRIEND, username.getBytes()));
        }
    }

    private void broadcastClientDetached(ClientHandler which) throws IOException {
        String username = String.format("%d;%s", Data.TYPE_CMD_FRIEND_REMOVED, which.username);
        for (ClientHandler clientHandler :  clientHandlers) {
            clientHandler.writeData(new Data(Data.TYPE_CMD_FRIEND, username.getBytes()));
        }
    }

    private ClientHandler getClientByUsername(String username) {
        synchronized (this) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.username.equals(username))
                    return clientHandler;
            }
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.close();
        }
    }

    private class ClientHandler extends ServerWorker {
        private boolean authenticated = false;
        private String username = null;

        public boolean isAuthenticated() {
            return authenticated;
        }

        public ClientHandler(Socket socket) throws IOException {
            super(socket);
        }

        @Override
        protected void processData(Data data) throws IOException {
            switch (data.getType()) {
                case Data.TYPE_ACC_LOGIN:
                    processRequestLogin(data.getExtra());
                    break;
                case Data.TYPE_CMD_LIST:
                    processRequestFriendsList();
                    break;
                case Data.TYPE_CMD_SNDFILE:
                    processSendingFile(data.getExtra());
                    break;
                case Data.TYPE_CMD_OK:
                    processResponseSendingFileRequest(data.getType(), data.getExtra());
                    break;
                case Data.TYPE_CMD_CANCEL:
                    processResponseSendingFileRequest(data.getType(), data.getExtra());
                    break;
            }
        }

        private void processResponseSendingFileRequest(byte type, byte[] extra) throws IOException {
            String rawString = new String(extra);
            synchronized (this) {
                StringTokenizer tokenizer = new StringTokenizer(rawString, "|");
                String who = tokenizer.nextToken();
                String uuid = tokenizer.nextToken();
                ClientHandler clientHandler = getClientByUsername(who);
                if (clientHandler != null)
                    clientHandler.writeData(new Data(type, uuid.getBytes()));
            }
        }

        private boolean checkAuthentication() throws IOException {
            if (!authenticated)
                writeData(new Data(Data.TYPE_CMD_DENIED, null));
            return authenticated;
        }

        private void requestSendingFile(FileHeader fileHeader) throws IOException {
            writeData(new Data(Data.TYPE_CMD_SNDFILE, fileHeader.getBytes()));
        }

        private void processSendingFile(byte[] extra) throws IOException {
            if (!checkAuthentication())
                return;
            FileHeader fileHeader = FileHeader.parse(extra);
            synchronized (ServerHandler.this) {
                ClientHandler clientHandler = getClientByUsername(fileHeader.getWho());
                if (clientHandler != null) {
                    fileHeader.setWho(username);
                    clientHandler.requestSendingFile(fileHeader);
                }
            }
        }

        @Override
        protected void onWorkerStopped() throws IOException {
            super.onWorkerStopped();
            removeClient(this);
        }

        private void processRequestFriendsList() throws IOException {
            if (!checkAuthentication())
                return;
            StringBuilder builder = new StringBuilder();
            synchronized (ServerHandler.this) {
                for (int i = 0; i < clientHandlers.size(); i++) {
                    ClientHandler clientHandler = clientHandlers.get(i);
                    if (!clientHandler.username.equals(username)) {
                        builder.append(clientHandlers.get(i).username);
                        if (i < clientHandlers.size() - 1)
                            builder.append(";");
                    }
                }
            }
            String friendList = builder.toString();
            writeData(new Data(Data.TYPE_CMD_LIST, friendList.getBytes()));
        }

        private void processRequestLogin(byte[] extra) throws IOException {
            Account account = Account.parse(extra, 0, extra.length);
            if (DataManager.getInstance().checkLogin(account.getUsername(), account.getPassword())) {
                authenticated = true;
                username = account.getUsername();
                addClient(this);
            }
            writeData(new Data(Data.TYPE_ACC_LOGIN, new byte[]{authenticated ? Data.TYPE_CMD_OK : Data.TYPE_CMD_CANCEL}));
        }
    }
}
