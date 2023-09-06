package com.newlife.Connect_multiple.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) {
        try {
            String[] cmd = {"cmd.exe", "/c", "start", "ping -t 192.168.1.1"};
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
//            Process process = builder.start();
            Process process = Runtime.getRuntime().exec("cmd.exe /c start ping -t 192.168.1.1");
//            process.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
}
