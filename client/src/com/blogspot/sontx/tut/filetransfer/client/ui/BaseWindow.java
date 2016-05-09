package com.blogspot.sontx.tut.filetransfer.client.ui;

import javax.swing.*;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public abstract class BaseWindow extends JFrame {
    public BaseWindow() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    public void showWindow() {
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }
    }

    static {
        setSystemLookAndFeel();
    }
}
