package com.newlife.Connect_multiple.controller;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class TestModuleInLinux {
    public static void main(String[] asrgs) {
//        Stream<ProcessHan>
//        Stream<ProcessHandle> process = ProcessHandle.allProcesses();
//        List<ProcessHandle> process_list = process.toList();
//        for (int i = 0; i < process_list.size(); i++) {
//            String command_text = process_list.get(i).info().command().orElse("").toString();
//        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject)jsonParser.parse(createJson());
            System.out.print(json.get("id"));
        }
        catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    private static String createJson() {
        String message = "";
        JSONObject jsonObject = new JSONObject();
        Long milisecond = System.currentTimeMillis();
        String id = milisecond + "clientId_2";
        String command = "ping -t 1.1.1.1";
        jsonObject.put("id", id);
        jsonObject.put("command", command);
        return jsonObject.toJSONString();
    }
}
