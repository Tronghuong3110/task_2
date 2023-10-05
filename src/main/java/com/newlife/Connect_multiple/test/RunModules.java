package com.newlife.Connect_multiple.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunModules {
    public static void main (String[] args) {
//        try {
            String[] cmd = {"cmd.exe", "/c", "start", "ping -t 192.168.1.1"};
            // taskkill /F /FI "Imagename eq ping.exe" /FI "CommandLine eq ping -t 1.1.1.1"
            // taskkill /F /FI 'Caption eq PING.EXE' /FI 'CommandLine eq ping -t 1.1.1.1'
            String killProcess = "taskkill /F /IM PING.EXE /FI 'WINDOWTITLE ping -t 1.1.1.1'";
            String processId = "' ";
//            ProcessBuilder builder = new ProcessBuilder("cmd.exe",
//                                                "wmic service where Name = 'ping -t 192.168.1.1' call startservice");
//            builder.redirectErrorStream(true);
//            Process process = builder.start();
//            Process process = Runtime.getRuntime().exec("cmd.exe /c start ping -t 192.168.1.1");
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("cmd.exe", "/c", killProcess);
                    Process process = processBuilder.start();
                    System.out.println("Dừng thành công");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
//            process.waitFor();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while((line = bufferedReader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
