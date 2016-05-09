package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.bo.Worker;
import com.blogspot.sontx.tut.filetransfer.client.FileWorker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public abstract class FileProcessingWindow extends BaseWindow implements FileWorker.OnProcessingFileListener, Worker.OnConnectionStateChangedListener {
    public static final int FILE_SERVER_PORT = 3393;
    private JProgressBar progressBar;
    private JLabel fileNameField;
    private JLabel processingField;
    private JButton btnPause;
    private FileWorker fileWorker;
    private boolean processingIsCompleted = false;

    private void initializeComponents() {
        setSize(415, 236);
        getContentPane().setLayout(null);

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 47, 379, 23);
        getContentPane().add(progressBar);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(300, 163, 89, 23);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCancel();
            }
        });
        getContentPane().add(btnCancel);

        btnPause = new JButton("Pause");
        btnPause.setBounds(201, 163, 89, 23);
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPause();
            }
        });
        getContentPane().add(btnPause);

        processingField = new JLabel("0B/0B");
        processingField.setHorizontalAlignment(SwingConstants.RIGHT);
        processingField.setBounds(243, 31, 146, 14);
        getContentPane().add(processingField);

        fileNameField = new JLabel("File name");
        fileNameField.setBounds(10, 31, 223, 14);
        getContentPane().add(fileNameField);
    }

    public FileProcessingWindow(String fileName) {
        initializeComponents();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        fileNameField.setText(fileName);
    }

    protected void setupWorker(FileWorker fileWorker) {
        this.fileWorker = fileWorker;
        fileWorker.setOnProcessingFileListener(this);
        fileWorker.setOnConnectionStateChangedListener(this);
    }

    private void processPause() {
        boolean wantToPause = btnPause.getText().equals("Pause");
        btnPause.setText(wantToPause ? "Resume" : "Pause");
        if (wantToPause)
            fileWorker.pauseWorker();
        else
            fileWorker.resumeWorker();
    }

    private void processCancel() {
        if (JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to cancel %s?", getTitle()),
                "Processing", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            fileWorker.cancel();
        }
    }

    @Override
    public void onProcessingFile(final long totalBytes, final long processedBytes) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setMaximum(100);
                progressBar.setValue((int) (processedBytes / (float)totalBytes * 100.0f));
                processingField.setText(String.format("%dB/%dB", processedBytes, totalBytes));
            }
        });
    }

    @Override
    public void onCompleted(FileWorker worker, final Object extra) {
        processingIsCompleted = true;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!fileWorker.canceled()) {
                    if (extra instanceof Exception) {
                        JOptionPane.showMessageDialog(FileProcessingWindow.this,
                                String.format("Can't %s, because: %s", getTitle(), ((Exception) extra).getMessage()),
                                "Processing", JOptionPane.ERROR_MESSAGE);
                        doCleanIfNecessary();
                    } else {
                        JOptionPane.showMessageDialog(FileProcessingWindow.this, String.format("Done: %s", getTitle()));
                    }
                }
                closeWorker();
                dispose();
            }
        });
    }

    private void closeWorker() {
        try {
            fileWorker.close();
        } catch (IOException e) {
        }
    }

    protected void doCleanIfNecessary() {}

    @Override
    public void onConnectionStateChanged(Worker worker, int state, Object extra) {
        if (state == Worker.CONNECTION_STATE_DISCONNECTED && !processingIsCompleted) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(FileProcessingWindow.this,
                            String.format("Connection broken down, so you can't %s", getTitle()),
                            "Processing", JOptionPane.ERROR_MESSAGE);
                    closeWorker();
                    doCleanIfNecessary();
                    dispose();
                }
            });
        }
    }
}