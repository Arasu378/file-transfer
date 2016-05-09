package com.blogspot.sontx.tut.filetransfer.bo;

import com.blogspot.sontx.tut.filetransfer.bean.Data;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 4/4/2016.
 */
public abstract class Worker extends Thread implements Closeable {
    public static final int DATA_IN_BUFFER_SIZE = 2048;
    public static final int DATA_OUT_BUFFER_SIZE = 2048;
    public static final int CONNECTION_STATE_CONNECTED = 1;
    public static final int CONNECTION_STATE_DISCONNECTED = 2;

    private InputStream in;
    private OutputStream out;
    private Socket socket;
    private OnConnectionStateChangedListener mOnConnectionStateChangedListener = null;

    public void setOnConnectionStateChangedListener(OnConnectionStateChangedListener l) {
        mOnConnectionStateChangedListener = l;
    }

    public void waitForReady(int timeout) throws InterruptedException, IOException {
        long startMillis = System.currentTimeMillis();
        while (!ready() && (System.currentTimeMillis() - startMillis < timeout)) {
            Thread.sleep(100);
        }
    }

    public boolean ready() throws IOException {
        return in.available() > 0;
    }

    private static int getDataSize(InputStream in) throws IOException {
        int b1 = in.read();
        int b2 = in.read();
        return (b1 << 8) | (b2);
    }

    public static Data readData(InputStream in) throws IOException {
        int receivedBytes = 0;
        int dateSize = getDataSize(in);
        if (dateSize < 0)
            return null;
        byte[] dataInBuffer = new byte[dateSize];
        do {
            int length = in.read(dataInBuffer, receivedBytes, dataInBuffer.length - receivedBytes);
            receivedBytes += length;
        } while (receivedBytes < dataInBuffer.length);
        Log.i(String.format("Received %d bytes", dataInBuffer.length));
        return Data.parse(dataInBuffer, 0, dataInBuffer.length);
    }

    public Data readData() throws IOException {
        return readData(in);
    }

    private void setDataSize(int size) throws IOException {
        out.write((size >> 8) & 0x000000ff);
        out.write(size & 0x000000ff);
    }

    public void writeData(Data data) throws IOException {
        byte[] dataBytes = data.getBytes();
        setDataSize(dataBytes.length);
        out.write(dataBytes);
        Log.i(String.format("Sent %d bytes", dataBytes.length));
    }

    public Data writeForResult(Data data) throws IOException {
        writeData(data);
        return readData();
    }

    protected void fireOnConnectionStateChangedEvent(int state, Object extra) {
        if (mOnConnectionStateChangedListener != null)
            mOnConnectionStateChangedListener.onConnectionStateChanged(this, state, extra);
    }

    protected abstract void runOnBackground() throws IOException;

    protected void initializeConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        fireOnConnectionStateChangedEvent(CONNECTION_STATE_CONNECTED, null);
    }

    protected void onWorkerStopped() throws IOException {}

    @Override
    public final void run() {
        try {
            runOnBackground();
        } catch (IOException e) {
            fireOnConnectionStateChangedEvent(CONNECTION_STATE_DISCONNECTED, e);
        }
        fireOnConnectionStateChangedEvent(CONNECTION_STATE_DISCONNECTED, null);
        try {
            onWorkerStopped();
        } catch (IOException e) {
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null)
            socket.close();
        Log.i("Socket closed!");
    }

    public interface OnConnectionStateChangedListener {
        void onConnectionStateChanged(Worker worker, int state, Object extra);
    }
}
