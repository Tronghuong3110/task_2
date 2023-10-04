package com.example.demo1;

public class ProcessInfo {
    private Long pId;
    private String command; // caption
    private String commandLine;
    private String argument;

    public ProcessInfo(Long pId, String command, String commandLine, String argument) {
        this.pId = pId;
        this.command = command;
        this.commandLine = commandLine;
        this.argument = argument;
    }

    public ProcessInfo(Long pId, String command, String commandLine) {
        this.pId = pId;
        this.command = command;
        this.commandLine = commandLine;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
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
