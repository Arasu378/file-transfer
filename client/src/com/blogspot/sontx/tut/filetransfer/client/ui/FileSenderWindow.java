package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.client.FileSender;
import com.blogspot.sontx.tut.filetransfer.client.FileWorker;
import com.blogspot.sontx.tut.filetransfer.client.Program;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class FileSenderWindow extends FileProcessingWindow {
    public FileSenderWindow(File sendingFile, String forWho, String uuid) {
        super(sendingFile.getName());
        setTitle(String.format("Sending %s from %s", sendingFile.getName(), forWho));
        try {
            FileSender fileSender = new FileSender(
                    Program.getInstance().getClient().getServerAddress(),
                    FILE_SERVER_PORT, uuid, sendingFile.getPath());
            setupWorker(fileSender);
            fileSender.start();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), getTitle(), JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
}
