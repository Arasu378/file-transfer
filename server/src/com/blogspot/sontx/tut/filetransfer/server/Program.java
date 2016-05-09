package com.blogspot.sontx.tut.filetransfer.server;

import java.io.IOException;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class Program {
    public static void main(String[] args) {
        try {
//            FileServerHandler fileServerHandler = new FileServerHandler("localhost", 3393);
//            fileServerHandler.start();
            ServerHandler serverHandler = new ServerHandler("localhost", 2512);
            serverHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
