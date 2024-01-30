package com.example.demo1.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.*;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class TestSSh {

    public static void main(String[] args ) {
        try {
//            System.out.println("ARG " + args[1]);
//            testBackUp(databaseName);
            String databaseName = args[1];
            System.out.println("============================================================");
            testReStore(databaseName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testBackUp(String databaseName) {
        String host = "192.168.79.128"; // Địa chỉ IP máy chủ SSH
        String username = "trong-huong"; // Tên người dùng SSH
        String password = "newlife123@"; // Mật khẩu SSH
        String sudoPassword = "newlife123@";
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22); // port mặc định là port 22
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // connect ssh server

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + sudoPassword + " | sudo -S ";
            String command = sudoCommand + "service clickhouse-server start; ";
            command += sudoCommand + "rm -r /var/lib/clickhouse/backup/*; ";
            command += sudoCommand + " rm -r /var/lib/clickhouse/shadow/*; ";
            command += sudoCommand + "clickhouse-backup create_remote -t test_restore_26_1_2024.* test_26_1_2024_backup_restore_2";
            channel.setCommand(command);
            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
        }
    }


    private static void testReStore(String databaseName) {
        String host = "192.168.79.128"; // Địa chỉ IP máy chủ SSH
        String username = "trong-huong"; // Tên người dùng SSH
        String password = "newlife123@"; // Mật khẩu SSH
        String sudoPassword = "newlife123@";
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, 22); // port mặc định là port 22
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(); // connect ssh server

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String sudoCommand = "echo " + sudoPassword + " | sudo -S ";
            String command = sudoCommand + "service clickhouse-server start; ";
            command += sudoCommand + "clickhouse-backup download " + databaseName;
            command += sudoCommand + "clickhouse-backup restore_remote " + databaseName;
            channel.setCommand(command);
            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}


