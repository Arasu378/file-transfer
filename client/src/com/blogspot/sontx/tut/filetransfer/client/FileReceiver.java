package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 4/4/2016.
 */
public class FileReceiver extends FileWorker {
    private FileOutputStream fileOutputStream;
    private long totalBytes = 0;

    public FileReceiver(String serverAddress, int serverPort, String uuid, String saveOnFile, long totalBytes) throws FileNotFoundException {
        super(serverAddress, serverPort, uuid);
        this.fileOutputStream = new FileOutputStream(saveOnFile);
        this.totalBytes = totalBytes;
    }

    private void receiveFile() throws IOException {
        Data okData = new Data(Data.TYPE_CMD_OK, null);
        long receivedBytes = 0;
        do {
            Data fileData = readData();
            if (fileData.getType() != Data.TYPE_FILE_DATA)
                throw new IOException("Remote refused");
            if (pendingCancel) {
                writeData(new Data(Data.TYPE_CMD_CANCEL, null));
                canceled = true;
                Log.e("Local cancel receive file!");
                break;
            }
            writeData(okData);
            fileOutputStream.write(fileData.getExtra());
            receivedBytes += fileData.getExtra().length;
            if (mOnProcessingFileListener != null)
                mOnProcessingFileListener.onProcessingFile(totalBytes, receivedBytes);
        } while (receivedBytes < totalBytes);
        Log.i("Finish receiving file!");
    }

    @Override
    protected void processingFile() throws IOException {
        receiveFile();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (fileOutputStream != null)
            fileOutputStream.close();
    }
}
