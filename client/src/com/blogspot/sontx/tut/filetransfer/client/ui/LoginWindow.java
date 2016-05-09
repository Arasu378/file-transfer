package com.blogspot.sontx.tut.filetransfer.client.ui;

import com.blogspot.sontx.tut.filetransfer.bean.Data;
import com.blogspot.sontx.tut.filetransfer.client.Client;
import com.blogspot.sontx.tut.filetransfer.client.Program;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 8/5/2016.
 */
public class LoginWindow extends ReconnectableWindow implements Client.OnReceivedResponseListener {
    private JPasswordField passwordField1;
    private JTextField usernameField1;
    private JTextField usernameField2;
    private JPasswordField passwordField2;
    private JPasswordField confirmField2;

    public LoginWindow() {
        setTitle("Login");
        setSize(380, 383);
        getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(59, 64, 77, 14);
        getContentPane().add(lblUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(59, 89, 77, 14);
        getContentPane().add(lblPassword);

        usernameField1 = new JTextField();
        usernameField1.setBounds(146, 61, 161, 20);
        getContentPane().add(usernameField1);
        usernameField1.setColumns(10);

        passwordField1 = new JPasswordField();
        passwordField1.setBounds(146, 86, 161, 20);
        getContentPane().add(passwordField1);

        JLabel lblHaventAnAccount = new JLabel("Haven't an account?");
        lblHaventAnAccount.setBounds(49, 164, 107, 14);
        getContentPane().add(lblHaventAnAccount);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processLogin();
            }
        });
        btnLogin.setBounds(218, 117, 89, 23);
        getContentPane().add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processRegister();
            }
        });
        btnRegister.setBounds(218, 273, 89, 23);
        getContentPane().add(btnRegister);

        JLabel label = new JLabel("Username:");
        label.setBounds(59, 192, 77, 14);
        getContentPane().add(label);

        JLabel label_1 = new JLabel("Password:");
        label_1.setBounds(59, 217, 77, 14);
        getContentPane().add(label_1);

        usernameField2 = new JTextField();
        usernameField2.setColumns(10);
        usernameField2.setBounds(146, 189, 161, 20);
        getContentPane().add(usernameField2);

        passwordField2 = new JPasswordField();
        passwordField2.setBounds(146, 214, 161, 20);
        getContentPane().add(passwordField2);

        JLabel lblLoginToSystem = new JLabel("Login to system:");
        lblLoginToSystem.setBounds(49, 39, 107, 14);
        getContentPane().add(lblLoginToSystem);

        JLabel lblConfirm = new JLabel("Confirm:");
        lblConfirm.setBounds(59, 245, 77, 14);
        getContentPane().add(lblConfirm);

        confirmField2 = new JPasswordField();
        confirmField2.setBounds(146, 242, 161, 20);
        getContentPane().add(confirmField2);

        Program.getInstance().getClient().setOnReceivedResponseListener(this);
    }

    private void processRegister() {

    }

    private void processLogin() {
        setEnabled(false);
        String username = usernameField1.getText();
        String password = new String(passwordField1.getPassword());
        try {
            Program.getInstance().getClient().requestLogin(username, password);
        } catch (IOException e) {
            reconnect(e);
        }
    }

    @Override
    public void loginResult(final byte result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (result == Data.TYPE_CMD_OK) {
                    MainWindow mainWindow = new MainWindow(usernameField1.getText());
                    mainWindow.showWindow();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Login failed!", getTitle(), JOptionPane.WARNING_MESSAGE);
                    setEnabled(true);
                }
            }
        });
    }

    @Override
    public void updateFriendList(String friend, int type) {

    }

    @Override
    public void hasFriendsList(String[] friends) {

    }

    @Override
    public String remoteRequestSendingFile(String fileName, long fileSize, String from) {
        return null;
    }

    @Override
    public void remoteCancelReceivingFile() {

    }

    @Override
    public void remoteAcceptReceivingFile(String from) {

    }
}
