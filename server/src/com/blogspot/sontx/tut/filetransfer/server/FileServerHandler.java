package com.blogspot.sontx.tut.filetransfer.server;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.bo.Log;
import com.blogspot.sontx.tut.filetransfer.bo.Worker;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class FileServerHandler extends Server implements FileBridge.OnFileBridgeClosedListener {
    private List<PairSocket> pairSockets = new ArrayList<>();
    private List<FileBridge> fileBridges = new ArrayList<>();

    public FileServerHandler(String serverAddress, int serverPort) throws IOException {
        super(serverAddress, serverPort);
        Log.i("File server handler is running...");
    }

    @Override
    protected void onAcceptSocket(Socket socket) throws IOException {
        Log.i(String.format("File server handler accepted connection: %s", socket.getRemoteSocketAddress()));
        PairSocket pairSocket = getPairSocket(socket);
        if (pairSocket != null) {
            Log.i("Valid pair socket");
            if (!tryToPair(pairSocket)) {
                pairSockets.add(pairSocket);
                Log.i("Can not pair, add to unpair list");
            } else {
                Log.i("Paired socket!");
            }
        } else {
            Log.e("Invalid pair socket!");
        }
    }

    private PairSocket getPairSocket(Socket socket) throws IOException {
        Data data = Worker.readData(socket.getInputStream());
        PairSocket pairSocket = null;
        if (data != null && data.getType() == Data.TYPE_FILE_UUID) {
            pairSocket = new PairSocket();
            pairSocket.socket = socket;
            pairSocket.uuid = new String(data.getExtra());
        } else {
            socket.close();
        }
        return pairSocket;
    }

    private boolean tryToPair(PairSocket pairSocket) {
        for (PairSocket _pairSocket : pairSockets) {
            if (_pairSocket.uuid.equals(pairSocket.uuid)) {
                try {
                    FileBridge bridge = new FileBridge(pairSocket.socket, _pairSocket.socket);
                    bridge.setOnFileBridgeClosedListener(this);
                    synchronized (this) {
                        fileBridges.add(bridge);
                    }
                    bridge.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFileBridgeClosed(FileBridge fileBridge) {
        synchronized (this) {
            fileBridges.remove(fileBridge);
        }
    }

    private class PairSocket {
        Socket socket;
        String uuid;
    }
}
