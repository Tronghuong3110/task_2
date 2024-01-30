package com.example.demo1.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class JsonUtil {
    public static JSONObject parseJson(String jsonObject) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(jsonObject);
            return json;
        }
        catch (Exception e) {
            System.out.println("Convert from jsonStr to jsonObject error");
            e.printStackTrace();
            return null;
        }
    }
    public static String createJson(String probeModuleJson, String message, String status_cmd,
                                    String statusModule, String title, String content, String pId, String caption) {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(probeModuleJson);
            jsonObject.put("message", message);
            jsonObject.put("statusCmd", status_cmd);
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("statusModule", statusModule);
            jsonObject.put("check", "true");
            jsonObject.replace("PID", pId);
            jsonObject.put("nameProcess", caption);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            System.out.println("Create json object error");
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonStatus(String message, JSONArray jsonArray, String idProbe, JSONArray memories) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("listStatus", jsonArray);
        jsonObject.put("idProbe", idProbe);
        jsonObject.put("check", "true");
        jsonObject.put("action", "getStatus");
        jsonObject.put("memories", memories);
        return jsonObject.toJSONString();
    }

    public static String createJsonGetCpu(JSONObject json) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "getCPU");
        jsonObject.put("cpuLoad", json);
        jsonObject.put("check", "true");
        return jsonObject.toJSONString();
    }
}
