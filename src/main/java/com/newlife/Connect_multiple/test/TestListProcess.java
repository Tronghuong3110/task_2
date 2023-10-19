package com.newlife.Connect_multiple.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class TestListProcess {
    public static void main(String[] args) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe",
                "WMIC path win32_process get Caption,Processid,Commandline");
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            String command = parts[0];
            String PID = parts[parts.length - 1];
            String commandLine = solveCommandLineInWindows(parts);
            if(!PID.trim().isEmpty()) {
                System.out.println("PID " +PID);
                System.out.println("CommandLine " + commandLine);
                System.out.println("======================================================================");
            }
        }
    }

    private static String solveCommandLineInWindows(String[] commandLine) {
        String command = "";
        for(int i = 1; i < commandLine.length-1; i++) {
            command += commandLine[i] + " ";
        }
        return command;
    }
}
