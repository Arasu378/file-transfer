package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bean.Account;
import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bean.FileHeader;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

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

    public void requestLogin(String username, String password) throws IOException {
        writeData(new Data(Data.TYPE_ACC_LOGIN, new Account(username, password).getBytes()));
    }

    public void requestSendFile(String filePath, String who) throws IOException {
        File file = new File(filePath);
        FileHeader fileHeader = new FileHeader(file, who);
        writeData(new Data(Data.TYPE_CMD_SNDFILE, fileHeader.getBytes()));
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
                case Data.TYPE_ACC_LOGIN:
                    Log.i("Received login result");
                    processLoginResult(data.getExtra());
                    break;
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
                    processRemoteCancelReceivingFile();
                    break;
                case Data.TYPE_CMD_OK:
                    Log.i("Remote accept receiving file");
                    processRemoteAcceptReceivingFile(data.getExtra());
                    break;
            }
        }
    }

    private void processRemoteAcceptReceivingFile(byte[] extra) {
        String rawString = new String(extra);
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.remoteAcceptReceivingFile(rawString);
    }

    private void processLoginResult(byte[] extra) {
        byte result = extra[0];
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.loginResult(result);
    }

    private void processRemoteCancelReceivingFile() {
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.remoteCancelReceivingFile();
    }

    private void processRequestSendFile(byte[] extra) throws IOException {
        FileHeader fileHeader = FileHeader.parse(extra);
        if (mOnReceivedResponseListener != null) {
            String uuid = mOnReceivedResponseListener.remoteRequestSendingFile(fileHeader.getFileName(), fileHeader.getFileLength(), fileHeader.getWho());
            if (uuid != null) {
                String st = String.format("%s|%s", fileHeader.getWho(), uuid);
                writeData(new Data(Data.TYPE_CMD_OK, st.getBytes()));
            } else {
                writeData(new Data(Data.TYPE_CMD_CANCEL, null));
            }
        }
    }

    private void processNewFriend(byte[] extra) {
        String rawString = new String(extra);
        String[] parts = rawString.split(";");
        if (mOnReceivedResponseListener != null)
            mOnReceivedResponseListener.updateFriendList(parts[1], Integer.parseInt(parts[0]));
    }

    private void processFriendsList(byte[] extra) {
        if (extra != null) {
            String rawString = new String(extra);
            String[] parts = rawString.split(";");
            if (mOnReceivedResponseListener != null)
                mOnReceivedResponseListener.hasFriendsList(parts);
        }
    }

    public interface OnReceivedResponseListener {
        void loginResult(byte result);
        void updateFriendList(String friend, int type);
        void hasFriendsList(String[] friends);
        String remoteRequestSendingFile(String fileName, long fileSize, String from);
        void remoteCancelReceivingFile();
        void remoteAcceptReceivingFile(String uuid);
    }
}
