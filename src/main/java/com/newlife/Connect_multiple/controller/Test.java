package com.newlife.Connect_multiple.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) {
//        try {
//            String[] cmd = {"cmd.exe", "/c", "start", "ping -t 192.168.1.1"};
//            ProcessBuilder builder = new ProcessBuilder(cmd);
//            builder.redirectErrorStream(true);
////            Process process = builder.start();
//            Process process = Runtime.getRuntime().exec("cmd.exe /c start ping -t 192.168.1.1");
////            process.waitFor();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line;
//            while((line = bufferedReader.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException  e) {
//            e.printStackTrace();
//        }
        try {
            // Sử dụng ProcessBuilder để chạy PowerShell và lệnh để lấy thông tin về tiến trình ping
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
//                    "Get-WmiObject Win32_Process | Where-Object { $_.CommandLine 'powershell.exe'} | Select-Object CommandLine");
//          WMIC path win32_process get Caption,Processid,Commandline
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
                                "WMIC path win32_process get Caption,Processid,Commandline");
            Process process = processBuilder.start();

            // Đọc kết quả từ quá trình
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
//            Integer count = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String caption = parts[0];
                    String processId = parts[parts.length-1];
                    String commandLine = solveCommandLine(parts);

                    if(commandLine.contains("ping -t 192.168.1.1")) {
                        System.out.println("Caption: " + caption);
                        System.out.println("Processid: " + processId);
                        System.out.println("CommandLine: " + commandLine);
//                        System.out.println(line);
                        System.out.println("====================================================");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String solveCommandLine(String[] commandLine) {
        String command = "";
        for(int i = 1; i < commandLine.length-1; i++) {
            command += commandLine[i] + " ";
        }
        return command;
    }
}


/*
*
*
* Stream<ProcessHandle> process = ProcessHandle.allProcesses();
        List<ProcessHandle> process_list = process.toList();
 for (int i=0;i<process_list.size();i++)
{
String command_text = process_list.get(i).info().command().orElse("").toString();
}
*
*
*
*
* */