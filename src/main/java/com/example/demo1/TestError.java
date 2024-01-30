package com.example.demo1;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

public class TestError {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String arg = sc.nextLine();
            try {
                String[] cmd = {"gnome-terminal", "--", "bash", "-c", arg + "; exec bash"};
                System.out.println("CMD " + Arrays.toString(cmd));
//            ProcessBuilder builder = new ProcessBuilder();
//            builder.command(cmd);

                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader output = getOutput(p);
                BufferedReader error = getError(p);
                String ligne = "";
                while ((ligne = output.readLine()) != null) {
                    System.out.println("OK " + ligne);
                }

                while ((ligne = error.readLine()) != null) {
                    System.out.println("Error " + ligne);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedReader getOutput(Process p) {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private static BufferedReader getError(Process p) {
        return new BufferedReader(new InputStreamReader(p.getErrorStream()));
    }
}
