package com.example.demo1.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
                                    String statusModule, String title, String content, String pId) {
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
            return jsonObject.toJSONString();
        } catch (Exception e) {
            System.out.println("Create json object error");
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonStatus(String message, JSONArray jsonArray, String idProbe) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("listStatus", jsonArray);
        jsonObject.put("idProbe", idProbe);
        jsonObject.put("check", "true");
        return jsonObject.toJSONString();
    }
}
