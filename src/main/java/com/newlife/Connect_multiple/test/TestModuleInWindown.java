package com.newlife.Connect_multiple.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestModuleInWindown {

    public static void main(String[] args) {
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
//                    "Get-WmiObject Win32_Process | Where-Object { $_.CommandLine 'powershell.exe'} | Select-Object CommandLine");
//          WMIC path win32_process get Caption,Processid,Commandline
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
//                                "Get-WmiObject -Class Win32_Process | Select-Object ProcessId,Name,Status");
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
                    "WMIC path win32_process get Caption,Processid,Commandline");
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
//                    "Get-WmiObject -Query 'SELECT * FROM Win32_Process ' ");
            Process process = processBuilder.start();

            // Đọc kết quả từ quá trình
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
////                if (parts.length >= 3) {
                    String caption = parts[0];
                    String processId = parts[parts.length-1];
                    String commandLine = solveCommandLine(parts);
                    System.out.println("caption " + caption);
                    System.out.println("CommandLine " + commandLine);
                    System.out.println("PID " + processId);
                    System.out.println("==================================================");
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