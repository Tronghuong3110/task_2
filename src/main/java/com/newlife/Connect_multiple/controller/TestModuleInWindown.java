package com.newlife.Connect_multiple.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestModuleInWindown {

    public static void main(String[] args) {
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
//                    "Get-WmiObject Win32_Process | Where-Object { $_.CommandLine 'powershell.exe'} | Select-Object CommandLine");
//          WMIC path win32_process get Caption,Processid,Commandline
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
                                "WMIC path win32_process get Caption,Processid,Commandline");
            Process process = processBuilder.start();

            // Đọc kết quả từ quá trình
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
//                if (parts.length >= 3) {
                    String caption = parts[0];
                    String processId = parts[parts.length-1];
                    String commandLine = solveCommandLine(parts);
                    String commandLineFromDb = "wininit"; // lấy từ database
                    if(caption.contains(commandLineFromDb)) { // kiểm tra module với command line có còn chạy không
                        System.out.println("Caption: " + caption);
                        System.out.println("Processid: " + processId);
                        System.out.println("CommandLine: " + commandLine);
                        System.out.println("====================================================");
                    }
//                }
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

* */