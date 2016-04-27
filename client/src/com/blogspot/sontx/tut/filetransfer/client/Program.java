package com.blogspot.sontx.tut.filetransfer.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Copyright 2016 by sontx
 * Created by xuans on 6/4/2016.
 */
public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int what = scanner.nextInt();
        FileWorker worker = null;
        if (what == 0) {
            System.out.println("sender");
            try {
                worker = new FileSender("localhost", 3393, "123", "E:\\data\\a.mp3");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("receiver");
            try {
                worker = new FileReceiver("localhost", 3393, "123", "E:\\data\\b.mp3", 4701048);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        worker.start();
        System.out.println("doing...");
        scanner.next();
    }
}
