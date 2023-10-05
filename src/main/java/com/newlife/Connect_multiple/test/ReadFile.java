package com.newlife.Connect_multiple.test;

import com.newlife.Connect_multiple.util.JsonUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class ReadFile {

    public static  void main(String[] args) {
        String path = "C:\\Users\\Laptop24h\\Downloads\\mysql-config.txt";
        readFile(path);
    }

    private static void readFile(String pathFile) {
        try {
            FileReader fileReader=new FileReader(pathFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                if(line.trim().startsWith("#")) {
                    continue;
                }
                String[] lines = line.split("=");
                if (lines.length >= 2) {
                    String key = lines[0].trim();
                    String value = lines[1].trim();
                    System.out.println("Key " + key);
                    System.out.println("Value " + value);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Read file error");
            e.printStackTrace();
        }
    }
}
