package com.newlife.Connect_multiple.controller;

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
                    "wmic process get Caption,Processid,Commandline");
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
//                    String commandLineFromDb = "wininit"; // lấy từ database
//                    if(caption.contains(commandLineFromDb)) { // kiểm tra module với command line có còn chạy không
                    List<String> strs = new ArrayList<>();
                    String tmp = "";
                    for(int i = 0; i < parts.length; i++) {
                        if(parts[i].equals("18920")) {
//                            System.out.print(" ++ Status ++ ");
//                            System.out.print(parts[i] + "+");
                        }
                        tmp += parts[i] + " ";
                    }
                    strs.add(tmp);
//                    System.out.println("====================================================");
//                    }
//                }
                for(String str : strs) {
//                    if(str.contains("18920")) {
                        System.out.println(str.toLowerCase().contains("running"));
                        System.out.println("================================ status ===============");
                        System.out.println(str);
                        System.out.println("================================ status ===============");
//                    }
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

* */