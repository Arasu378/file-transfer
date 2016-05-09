package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.client.Client;
import com.blogspot.sontx.tut.filetransfer.client.FileSender;
import com.blogspot.sontx.tut.filetransfer.client.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public class MainWindow extends ReconnectableWindow implements Client.OnReceivedResponseListener {
    private JList<String> friendField;
    private DefaultListModel<String> friendModel;
    private FileTemp currentSendingFileTemp = new FileTemp();

    public MainWindow(String username) {
        setTitle(username);
        setResizable(true);
        addWindowListener(new WindowEventHandler());
        friendField = new JList<>();
        JScrollPane scrollPane = new JScrollPane(friendField);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setSize(304, 582);
        Program.getInstance().getClient().setOnReceivedResponseListener(this);
        setupFriendList();
    }

    private void setupFriendList() {
        friendModel = new DefaultListModel<>();
        friendField.setModel(friendModel);
        friendField.addMouseListener(new FriendClickHandler());
        try {
            Program.getInstance().getClient().requestFriendList();
        } catch (IOException e) {
            reconnect(e);
        }
    }

    @Override
    public void loginResult(byte result) {

    }

    @Override
    public void updateFriendList(final String friend, final int type) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (type == Data.TYPE_CMD_FRIEND_ADDED)
                    friendModel.addElement(friend);
                else if (type == Data.TYPE_CMD_FRIEND_REMOVED)
                    friendModel.removeElement(friend);
            }
        });
    }

    @Override
    public void hasFriendsList(final String[] friends) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (String friend : friends) {
                    friendModel.addElement(friend);
                }
            }
        });
    }

    private String tempUUID;
    @Override
    public String remoteRequestSendingFile(final String fileName, final long fileSize, final String from) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    tempUUID = null;
                    if (JOptionPane.showConfirmDialog(MainWindow.this, String.format(
                            "%s want to sending %s(%.2fKB), accept?", from, fileName, fileSize / 1024.0f),
                            getTitle(), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                        tempUUID = UUID.randomUUID().toString();
                        FileReceiverWindow fileReceiverWindow = new FileReceiverWindow(fileName, fileSize, from, tempUUID);
                        fileReceiverWindow.showWindow();
                    }
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return tempUUID;
    }

    @Override
    public void remoteCancelReceivingFile() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(MainWindow.this, String.format("%s canceled receiving '%s'!",
                        currentSendingFileTemp.forWho, currentSendingFileTemp.file.getName()), getTitle(), JOptionPane.WARNING_MESSAGE);
                friendField.setEnabled(true);
            }
        });
    }

    @Override
    public void remoteAcceptReceivingFile(final String uuid) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                friendField.setEnabled(true);
                FileSenderWindow fileSenderWindow = new FileSenderWindow(currentSendingFileTemp.file, currentSendingFileTemp.forWho, uuid);
                fileSenderWindow.showWindow();
            }
        });
    }

    private static class FileTemp {
        public String forWho;
        public File file;
    }

    private class FriendClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int where = friendField.locationToIndex(e.getPoint());
                String who = friendModel.get(where);
                pickAndSendFile(who);
            }
        }

        private void pickAndSendFile(String forWho) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(String.format("Pick a file to send to %s", forWho));
            if (fileChooser.showOpenDialog(MainWindow.this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    friendField.setEnabled(false);
                    Program.getInstance().getClient().requestSendFile(selectedFile.getPath(), forWho);
                    currentSendingFileTemp.file = selectedFile;
                    currentSendingFileTemp.forWho = forWho;
                } catch (IOException e) {
                    reconnect(e);
                }
            }
        }
    }

    private static class WindowEventHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            try {
                Program.getInstance().getClient().close();
            } catch (IOException e1) {
            }
        }
    }
}
