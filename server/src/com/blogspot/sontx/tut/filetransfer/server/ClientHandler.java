package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Data;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public class ClientHandler extends ServerWorker {
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
        }
    }

    private void processSendingFile(byte[] extra) {
        String rawString = new String(extra);
        String[] parts = rawString.split("|");
        List<ClientHandler> clientHandlers = ServerHandler.getCurrentInstance().getClientHandlers();
        synchronized (ServerHandler.getCurrentInstance()) {
            ClientHandler receivingHandler = null;
            for (ClientHandler clientHandler : clientHandlers) {

            }
        }
    }

    private void processRequestFriendsList() throws IOException {
        StringBuilder builder = new StringBuilder();
        List<ClientHandler> clientHandlers = ServerHandler.getCurrentInstance().getClientHandlers();
        synchronized (ServerHandler.getCurrentInstance()) {
            for (int i = 0; i < clientHandlers.size() - 1; i++) {
                builder.append(clientHandlers.get(i).username);
                builder.append("|");
            }
            if (clientHandlers.size() > 0)
                builder.append(clientHandlers.get(clientHandlers.size() - 1).username);
        }
        writeData(new Data(Data.TYPE_CMD_LIST, builder.toString().getBytes()));
    }

    private void processRequestLogin(byte[] extra) throws IOException {
        String rawString = new String(extra);
        String[] parts = rawString.split("|");
        if (DataManager.getInstance().checkLogin(parts[0], parts[1])) {
            authenticated = true;
            username = parts[0];
        }
        writeData(new Data(authenticated ? Data.TYPE_CMD_OK : Data.TYPE_CMD_CANCEL, null));
    }
}
