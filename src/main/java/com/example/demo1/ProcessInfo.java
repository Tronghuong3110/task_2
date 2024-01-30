package com.example.demo1;

public class ProcessInfo {
    private String pId;
    private String command; // caption
    private String commandLine;
    private String argument;

    public ProcessInfo(String pId, String command, String commandLine, String argument) {
        this.pId = pId;
        this.command = command;
        this.commandLine = commandLine;
        this.argument = argument;
    }

    public ProcessInfo(String pId, String command, String commandLine) {
        this.pId = pId;
        this.command = command;
        this.commandLine = commandLine;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }
}
