package com.example.demo1;

import java.util.List;
import java.util.stream.Stream;

public class Test1 {

    public static void main(String[] args) {
        try {
            // gnome-terminal -- bash -c "ping 1.1.1.1; exec bash"
            String [] cmd ={"gnome-terminal", "--", "bash", "-c", "ping 1.1.1.1; exec bash"};
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmd);
            Process process = builder.start();
            System.out.println("Success");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
