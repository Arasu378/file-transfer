package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;
import com.blogspot.sontx.tut.filetransfer.bo.Worker;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class FileBridge extends Thread implements Closeable {
    private Socket socket1;
    private Socket socket2;
    private OnFileBridgeClosedListener mOnFileBridgeClosedListener = null;

    public void setOnFileBridgeClosedListener(OnFileBridgeClosedListener l) {
        mOnFileBridgeClosedListener = l;
    }

    public FileBridge(Socket socket1, Socket socket2) throws IOException {
        this.socket1 = socket1;
        this.socket2 = socket2;
    }

    @Override
    public void run() {
        try {
            Pipe pipe1 = new Pipe(socket1.getInputStream(), socket2.getOutputStream());
            Pipe pipe2 = new Pipe(socket2.getInputStream(), socket1.getOutputStream());
            Log.i("Start pipe1");
            pipe1.start();
            Log.i("Start pipe2");
            pipe2.start();
            pipe1.join();
            Log.i("Pipe1 stopped");
            pipe2.join();
            Log.i("Pipe2 stopped");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
        if (mOnFileBridgeClosedListener != null)
            mOnFileBridgeClosedListener.onFileBridgeClosed(this);
    }

    @Override
    public void close() throws IOException {
        socket1.close();
        socket2.close();
    }

    private static class Pipe extends Thread {
        private InputStream in;
        private OutputStream out;

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[Worker.DATA_IN_BUFFER_SIZE];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                    Log.i(String.format("Forward %d bytes", length));
                }
            } catch (IOException e) {
                Log.e("Pipe stopped because: " + e.getMessage());
            }
        }

        public Pipe(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }
    }

    public interface OnFileBridgeClosedListener {
        void onFileBridgeClosed(FileBridge fileBridge);
    }
}
