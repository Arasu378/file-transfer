package com.blogspot.sontx.tut.filetransfer.client.ui;

import javax.swing.*;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public abstract class ReconnectableWindow extends BaseWindow {
    protected void reconnect(Exception what) {
        JOptionPane.showMessageDialog(this, what.getMessage(), getTitle(), JOptionPane.WARNING_MESSAGE);
        ConnectionWindow connectionWindow = new ConnectionWindow();
        connectionWindow.showWindow();
        dispose();
    }
}
