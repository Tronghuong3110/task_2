package com.example.demo1;

import com.fasterxml.jackson.core.SerializableString;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestSMS {
    public static void main(String[] args) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        try {
            String pathFile = getArgument(args);
            FileReader reader = new FileReader(pathFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.startsWith("Index")) {
                    String[] index = line.split(": ");
                    if(!map.containsKey(index[1])){
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(index[2]);
                        map.put(index[1], arrayList);
                        continue;
                    }
                    ArrayList<String> arrayList = map.get(index[1]);
                    arrayList.add(index[2]);
                    map.put(index[1], arrayList);
                }
            }
            Long count = 0L;
            FileWriter myWriter = new FileWriter("count.txt");
            for(String index:map.keySet()) {
                myWriter.write("Index: " + index + "\n");
                for(String key : map.get(index)) {
                    myWriter.write(key);
                    myWriter.write("\n");
                    count++;
                }
                myWriter.write("============================================================================= \n");
            }
            myWriter.write("Tổng số thiết bị: " + map.size() + "\n");
            myWriter.write("Tổng số bản tin: " + count + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getArgument(String[] args) {
        Options options = new Options();
        Option input = new Option("f", "file", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        String inputFilePath = cmd.getOptionValue("file");
        if(inputFilePath == null) {
            return System.getProperty("user.dir");
        }
        return inputFilePath;
    }
}
