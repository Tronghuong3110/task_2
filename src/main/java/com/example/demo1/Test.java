package com.example.demo1;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
//        SystemInfo systemInfo = new SystemInfo();
//        CentralProcessor processor = systemInfo.getHardware().getProcessor();
//
//            while(true) {
//                long[][] current_ticks = processor.getProcessorCpuLoadTicks();
//                System.out.println("CPU Load theo System info:");
//                for (int i = 0; i < current_ticks.length; i++) {
//                    long user =current_ticks[i][CentralProcessor.TickType.USER.getIndex()];
//                    long nice =current_ticks[i][CentralProcessor.TickType.NICE.getIndex()];
//                    long system =current_ticks[i][CentralProcessor.TickType.SYSTEM.getIndex()];
//                    long idle =current_ticks[i][CentralProcessor.TickType.IDLE.getIndex()];
//                    long total = user + nice + system + idle;
//                    long cpu_performance = total - idle;
//                    double cpuLoad = (double)cpu_performance / total;
//                    System.out.println("Core " + i + ": " + (cpuLoad * 100) + "%");
//
//                }
//                try {
//                    Thread.sleep(5000);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
        // JavaSysMon: com.jezhumble.javasysmon.JavaSysMon;
        // HypervisorDetector: org.vngx.jsch.HypervisorDetector;

        test();
    }
//

    // đọc dung lượng bộ nhớ của máy
    private static void readMemory() {
        // lấy ra dung lượng còn trống của mỗi đĩa
        for(Path root : FileSystems.getDefault().getRootDirectories()) {
            System.out.print(root + " : "); // Tên disk
            try {
                FileStore store = Files.getFileStore(root);
                System.out.print(String.format("Còn trống = %.1f", Double.valueOf(store.getUsableSpace()) / 1073741824));
                System.out.println(String.format(", Tổng cộng = %.1f", Double.valueOf(store.getTotalSpace()) / 1073741824));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
//                if (networkInterface.getDisplayName().startsWith("Intel(R) Wireless")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
//                        if (inetAddress.isSiteLocalAddress()) {
                            System.out.println("Name " + inetAddress.getHostName());
                            System.out.println("Host address " + inetAddress.getHostAddress());
                            System.out.println("IP Address: " + inetAddress.getHostAddress());
                            System.out.println("===================================================");
                            // Return the IP address if needed
//                            return inetAddress.getHostAddress();
//                        }
                    }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static double[] getCPULoad() throws IOException {
        Process process = Runtime.getRuntime().exec("mpstat -P ALL 1 1");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String line = null;
        String current_date = formatter.format(LocalDateTime.now());
        while ((line = reader.readLine()) != null) {
            if(!line.contains("Average") && !line.contains("all") && !line.contains("CPU") && !line.equals(" ")) {
                String[] cpu_loads = line.trim().split("\\s+");
                if(cpu_loads.length >= 4) {
                    try {
                        System.out.println("Thời gian " + current_date + " " + cpu_loads[0]);
                        System.out.println("Core " + cpu_loads[1]);
                        Double total = (double) Math.round((Double.valueOf(cpu_loads[cpu_loads.length - 1].trim().replaceAll(",", ".")) * 100) / 100);
                        System.out.println("Cpu Load " + (100 - total));
                        System.out.println("===========================================");
//                        for(int i = 0; i < cpu_loads.length ; i++) {
//                            System.out.println("Core " + i + " " + cpu_loads[i]);
//                        }
//                        System.out.println("Len " + cpu_loads.length);
//                        System.out.println("Core " + cpu_loads[1]);
//                        System.out.println(line);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new double[100];
    }
    private static void test() {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            String[] cmd = {"cat", "/proc/stat"};
            builder.command(cmd);
            Process process = builder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("cpu")) {
                    System.out.println("================================================================");
                    System.out.println(line);
                    String[] tmp = line.split("\\s+");
                    double total = 0;
                    for(int i = 1; i < tmp.length; i++) {
                        try {
                            double cpu = Double.parseDouble(tmp[i].trim());
                            total += cpu;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(tmp[0] + " " + ((Double.parseDouble(tmp[4]) / total ) * 100) );
                    System.out.println(tmp[4]);
                    System.out.println("================================================================");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

//try {
//                Thread.sleep(1000); // Delay for 1 second
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            try {
//                System.out.println("CPU theo mpstat: ");
//                double[] cpuLoad = getCPULoad();
//                System.out.println("CPU Load:");
//                for (int i = 0; i < cpuLoad.length; i++) {
////                    System.out.println("Core " + i + ": " + cpuLoad[i] + "%");
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }


// chỉ dùng cho java 6
//        JavaSysMon javaSysMon = new JavaSysMon();
//        CpuTimes cpuTimes = javaSysMon.cpuTimes();
//        for (int i = 0; i < cpuTimes.getCpuUsage().length; i++) {
//            System.out.println("Core " + i + " Usage: " + cpuTimes.getCpuUsage()[i] * 100 + "%");
//        }