package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.bo.Worker;
import com.blogspot.sontx.tut.filetransfer.client.Program;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 9/5/2016.
 */
public class ConnectionWindow extends BaseWindow implements Worker.OnConnectionStateChangedListener {
    private JTextField addressField;
    private JTextField portField;

    public ConnectionWindow() {
        setTitle("Connection");
        getContentPane().setLayout(null);

        JLabel lblServerAddress = new JLabel("Server address:");
        lblServerAddress.setBounds(53, 64, 103, 14);
        getContentPane().add(lblServerAddress);

        addressField = new JTextField();
        addressField.setText("localhost");
        addressField.setBounds(141, 61, 103, 20);
        getContentPane().add(addressField);
        addressField.setColumns(10);

        portField = new JTextField();
        portField.setEditable(false);
        portField.setText("2512");
        portField.setBounds(141, 92, 103, 20);
        getContentPane().add(portField);
        portField.setColumns(10);

        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processConnect();
            }
        });
        btnConnect.setBounds(155, 123, 89, 23);
        getContentPane().add(btnConnect);

        JLabel lblServerPort = new JLabel("Server port:");
        lblServerPort.setBounds(53, 95, 69, 14);
        getContentPane().add(lblServerPort);

        setSize(374, 255);
    }

    private void processConnect() {
        setEnabled(false);
        String address = addressField.getText();
        String sPort = portField.getText();
        int port = Integer.parseInt(sPort);
        Program.getInstance().setupClient(address, port);
        Program.getInstance().getClient().setOnConnectionStateChangedListener(this);
        Program.getInstance().getClient().start();
    }

    @Override
    public void onConnectionStateChanged(Worker worker, final int state, Object extra) {
        Program.getInstance().getClient().setOnConnectionStateChangedListener(null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (state == Worker.CONNECTION_STATE_CONNECTED) {
                    LoginWindow loginWindow = new LoginWindow();
                    loginWindow.showWindow();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(ConnectionWindow.this, "Can not connect to server.", getTitle(), JOptionPane.WARNING_MESSAGE);
                    setEnabled(true);
                }
            }
        });
    }
}
