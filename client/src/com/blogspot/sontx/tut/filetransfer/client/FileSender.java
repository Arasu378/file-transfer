package com.blogspot.sontx.tut.filetransfer.client;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 4/4/2016.
 */
public class FileSender extends FileWorker {
    private FileInputStream fileInputStream;
    private File originFile;

    public FileSender(String serverAddress, int serverPort, String uuid, String filePath) throws FileNotFoundException {
        super(serverAddress, serverPort, uuid);
        fileInputStream = new FileInputStream(originFile = new File(filePath));
    }

    private void sendFile() throws IOException {
        Data fileData = new Data(Data.TYPE_FILE_DATA, null);
        byte[] fileBuffer = new byte[DATA_IN_BUFFER_SIZE - 1];
        int chunk;
        long totalBytes = originFile.length();
        long sentBytes = 0;
        while ((chunk = fileInputStream.read(fileBuffer)) > 0) {
            fileData.setExtra(fileBuffer, 0, chunk);
            Data responseData = writeForResult(fileData);
            if (responseData.getType() != Data.TYPE_CMD_OK)
                throw new IOException("Remote refused");
            if (pendingCancel) {
                writeData(new Data(Data.TYPE_CMD_CANCEL, null));
                canceled = true;
                Log.e("Local cancel receive file!");
                break;
            }
            sentBytes += chunk;
            if (mOnProcessingFileListener != null)
                mOnProcessingFileListener.onProcessingFile(totalBytes, sentBytes);
        }
        Log.i("Finish sending file!");
    }

    @Override
    protected void processingFile() throws IOException {
        sendFile();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (fileInputStream != null)
            fileInputStream.close();
    }
}
