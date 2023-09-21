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
            jsonObject.put("idProbeModule", probeModuleEntity.getId());
            jsonObject.put("idCmdHistory", idCmdHistory);
            jsonObject.put("id_probe", probeModuleEntity.getIdProbe());
            jsonObject.put("path", probeModuleEntity.getPath());
            jsonObject.put("caption", probeModuleEntity.getCaption());
            jsonObject.put("arg", probeModuleEntity.getArg());
            jsonObject.put("cmd_win", cmdWin.orElse(probeModuleEntity.getCommand()));
            jsonObject.put("cmd_linux", cmdLinux.orElse(probeModuleEntity.getCommand()));
            jsonObject.put("pathLog", probeModuleEntity.getPathLog());
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
}
