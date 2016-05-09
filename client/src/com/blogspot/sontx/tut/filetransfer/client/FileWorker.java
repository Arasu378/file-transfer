package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 4/4/2016.
 */
public abstract class FileWorker extends ClientWorker {
    protected OnProcessingFileListener mOnProcessingFileListener = null;
    protected boolean pendingCancel = false;
    protected boolean pendingPause = false;
    protected boolean canceled = false;
    protected final Object lock = new Object();
    private String uuid;

    public void pauseWorker() {
        pendingPause = true;
    }

    public void resumeWorker() {
        pendingPause = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    public void cancel() {
        pendingCancel = true;
    }

    public boolean canceled() {
        return canceled;
    }

    public void setOnProcessingFileListener(OnProcessingFileListener l) {
        mOnProcessingFileListener = l;
    }

    protected abstract void processingFile() throws IOException, InterruptedException;

    @Override
    protected void runOnBackground() throws IOException {
        super.runOnBackground();
        try {
            Log.i("Start processing file...");
            processingFile();
            Log.i("Stopped processing file");
            if (mOnProcessingFileListener != null)
                mOnProcessingFileListener.onCompleted(this, null);
        } catch (IOException | InterruptedException e) {
            if (mOnProcessingFileListener != null)
                mOnProcessingFileListener.onCompleted(this, e);
        }
    }

    public FileWorker(String serverAddress, int serverPort, String uuid) {
        super(serverAddress, serverPort);
        this.uuid = uuid;
    }

    @Override
    protected void initializeConnection(Socket socket) throws IOException {
        super.initializeConnection(socket);
        writeData(new Data(Data.TYPE_FILE_UUID, uuid.getBytes()));
    }

    public interface OnProcessingFileListener {
        void onProcessingFile(long totalBytes, long processedBytes);
        void onCompleted(FileWorker worker, Object extra);
    }
}
