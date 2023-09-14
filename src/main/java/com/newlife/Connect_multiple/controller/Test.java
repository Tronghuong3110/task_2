package com.newlife.Connect_multiple.controller;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.Callback;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, new HashMap<String, Object>());

        int GetWindowTextW(Pointer hWnd, char[] lpString, int nMaxCount);

        int EnumWindows(WndEnumProc lpEnumFunc, Pointer arg);

        boolean IsWindowVisible(Pointer hWnd);
    }

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class, new HashMap<String, Object>());

        int GetWindowTextW(Pointer hWnd, char[] lpString, int nMaxCount);

        int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);
    }

    public interface WndEnumProc extends Callback {
        boolean callback(Pointer hWnd, Pointer arg);
    }

    public static class WinInfo {
        public String title;
        public int pid;

        public WinInfo(String title, int pid) {
            this.title = title;
            this.pid = pid;
        }
    }

    public static List<WinInfo> getWindowTitles() {
        final List<WinInfo> windowList = new ArrayList<>();
        User32 user32 = User32.INSTANCE;
        Kernel32 kernel32 = Kernel32.INSTANCE;

        user32.EnumWindows(new WndEnumProc() {
            public boolean callback(Pointer hWnd, Pointer arg) {
                if (user32.IsWindowVisible(hWnd)) {
                    char[] windowText = new char[512];
                    user32.GetWindowTextW(hWnd, windowText, 512);
                    String title = new String(windowText);
                    if (!title.isEmpty()) {
                        IntByReference pid = new IntByReference();
                        kernel32.GetWindowThreadProcessId(hWnd, pid);
                        windowList.add(new WinInfo(title, pid.getValue()));
                    }
                }
                return true;
            }
        }, null);

        return windowList;
    }

    public static void main(String[] args) {
        List<WinInfo> windowList = getWindowTitles();
        for (WinInfo info : windowList) {
            System.out.println("Title: " + info.title + ", PID: " + info.pid);
        }
    }
}
