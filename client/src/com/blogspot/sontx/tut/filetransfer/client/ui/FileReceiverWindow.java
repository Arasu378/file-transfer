package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.client.FileReceiver;
import com.blogspot.sontx.tut.filetransfer.client.Program;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public class FileReceiverWindow extends FileProcessingWindow {
    private File saveOnFile;

    public FileReceiverWindow(String fileName, long fileSize, String fromWho, String uuid) {
        super(fileName);
        setTitle(String.format("Receiving %s from %s", fileName, fromWho));
        String workingDir = System.getProperty("user.dir");
        File dataDir = new File(workingDir, "ReceivingFiles");
        if (!dataDir.isDirectory())
            dataDir.mkdirs();
        saveOnFile = new File(dataDir, fileName);
        try {
            FileReceiver fileReceiver = new FileReceiver(Program.getInstance().getClient().getServerAddress(),
                    FILE_SERVER_PORT, uuid, saveOnFile.getPath(), fileSize);
            setupWorker(fileReceiver);
            fileReceiver.start();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), getTitle(), JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    @Override
    protected void doCleanIfNecessary() {
        super.doCleanIfNecessary();
        saveOnFile.delete();
    }
}
