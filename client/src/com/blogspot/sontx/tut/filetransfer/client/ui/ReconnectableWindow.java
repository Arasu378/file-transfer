package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.client.Client;

import javax.swing.*;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public abstract class ReconnectableWindow extends BaseWindow implements Client.OnReceivedResponseListener {
    void reconnect(Exception what) {
        JOptionPane.showMessageDialog(this, what.getMessage(), getTitle(), JOptionPane.WARNING_MESSAGE);
        ConnectionWindow connectionWindow = new ConnectionWindow();
        connectionWindow.showWindow();
        dispose();
    }

    @Override
    public void loginResult(byte result) {

    }

    @Override
    public void updateFriendList(String friend, int type) {

    }

    @Override
    public void hasFriendsList(String[] friends) {

    }

    @Override
    public String remoteRequestSendingFile(String fileName, long fileSize, String from) {
        return null;
    }

    @Override
    public void remoteCancelReceivingFile() {

    }

    @Override
    public void remoteAcceptReceivingFile(String uuid) {

    }

    @Override
    public void registerResult(byte result, String extraMessage) {

    }
}
