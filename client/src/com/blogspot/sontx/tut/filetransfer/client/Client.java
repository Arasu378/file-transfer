package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bean.Account;
import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 10/4/2016.
 */
public final class Client extends ClientWorker {
    private OnReceivedResponseListener mOnReceivedResponseListener = null;

    public void setOnReceivedResponseListener(OnReceivedResponseListener l) {
        mOnReceivedResponseListener = l;
    }

    public void requestFriendList() throws IOException {
        writeData(new Data(Data.TYPE_CMD_LIST, null));
    }

    public void requestSendFile(String filePath, String who) throws IOException {
        File file = new File(filePath);
        String extra = String.format("%s|%s|%d", who, file.getName(), file.length());
        writeData(new Data(Data.TYPE_CMD_SNDFILE, extra.getBytes()));
    }

    public Client(String serverAddress, int serverPort) {
        super(serverAddress, serverPort);
    }

    @Override
    protected void runOnBackground() throws IOException {
        super.runOnBackground();
        while (true) {
            Data data = readData();
            switch (data.getType()) {
                case Data.TYPE_CMD_LIST:
                    Log.i("Received friends list");
                    processFriendsList(data.getExtra());
                    break;
                case Data.TYPE_CMD_FRIEND:
                    Log.i("A friend joined");
                    processNewFriend(data.getExtra());
                    break;
                case Data.TYPE_CMD_SNDFILE:
                    Log.i("A request send file from remote");
                    processRequestSendFile(data.getExtra());
                    break;
                case Data.TYPE_CMD_CANCEL:
                    Log.i("Remote cancel receiving file");
                    processRemoteCancelReceivingFile(data.getExtra());
                    break;
            }
        }
    }

    private void processRemoteCancelReceivingFile(byte[] extra) {
        String rawString = new String(extra);
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.remoteCancelReceivingFile(rawString);
    }

    private void processRequestSendFile(byte[] extra) throws IOException {
        String rawString = new String(extra);
        String[] parts = rawString.split("|");
        String from = parts[0];
        String fileName = parts[1];
        String sFileSize = parts[2];
        long fileSize = Long.parseLong(sFileSize);
        if (mOnReceivedResponseListener != null) {
            String uuid = mOnReceivedResponseListener.requestSendFile(fileName, fileSize, from);
            if (uuid != null)
                writeData(new Data(Data.TYPE_CMD_OK, uuid.getBytes()));
        }
    }

    private void processNewFriend(byte[] extra) {
        String rawString = new String(extra);
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.updateFriendList(rawString);
    }

    private void processFriendsList(byte[] extra) {
        String rawString = new String(extra);
        String[] parts = rawString.split("|");
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.hasFriendsList(parts);
    }

    public interface OnReceivedResponseListener {
        void updateFriendList(String newFriend);
        void hasFriendsList(String[] friends);
        String requestSendFile(String fileName, long fileSize, String from);
        void remoteCancelReceivingFile(String from);
    }
}
