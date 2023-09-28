package com.newlife.Connect_multiple.util;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.util.Optional;

public class JsonUtil {
    public static String createJson(ProbeModuleEntity probeModuleEntity, String idCmdHistory, Optional<String> cmdWin, Optional<String> cmdLinux) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idProbeModule", probeModuleEntity.getId().toString());
            jsonObject.put("idCmdHistory", idCmdHistory);
            jsonObject.put("id_probe", probeModuleEntity.getIdProbe().toString());
            jsonObject.put("path", probeModuleEntity.getPath());
            jsonObject.put("caption", probeModuleEntity.getCaption());
            jsonObject.put("arg", probeModuleEntity.getArg());
            jsonObject.put("cmd_win", cmdWin.orElse(probeModuleEntity.getCommand()));
            jsonObject.put("cmd_linux", cmdLinux.orElse(probeModuleEntity.getCommand()));
            jsonObject.put("pathLog", probeModuleEntity.getPathLog());
            jsonObject.put("moduleName", probeModuleEntity.getModuleName());
            return jsonObject.toJSONString();
        }
        catch (NullPointerException e) {
            System.out.println("Create json fail");
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject parseJson(String jsonObject) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonObject);
            return json;
        }
        catch (Exception e) {
            System.out.println("Parse from String to Json error");
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject createJsonResponse(String message, String status) {
        try{
            JSONObject res = new JSONObject();
            res.put("message", message);
            res.put("status", status);
            return res;
        }
        catch (Exception e) {
            System.out.println("Create response to front error");
            e.printStackTrace();
            return null;
        }
    }
}
