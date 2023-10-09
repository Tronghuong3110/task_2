package com.newlife.Connect_multiple.util;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Optional;

public class JsonUtil {
    public static String createJson(ProbeModuleEntity probeModuleEntity, String idCmdHistory,
                                    Optional<String> cmdWin, Optional<String> cmdLinux, String action) {
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
            jsonObject.put("PID", probeModuleEntity.getProcessId());
            jsonObject.put("CommandLine", probeModuleEntity.getCommand()); // câu lệnh dùng để dừng module
            jsonObject.put("nameProcess", probeModuleEntity.getProcessName()); // tên process để dừng
            jsonObject.put("action", action);
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

    public static String createJsonStatus(String action, List<ProbeModuleEntity> probeModuleEntities) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        Integer idProbe = -1;
        JSONArray jsonArray = new JSONArray();
        for (ProbeModuleEntity probeModule : probeModuleEntities) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id_probe_module", probeModule.getId().toString());
            jsonObject1.put("path_log", probeModule.getPathLog());
            jsonObject1.put("PID", probeModule.getProcessId());
            jsonObject1.put("cmd_win", probeModule.getCommand());
            jsonObject1.put("cmd_linux", probeModule.getCommand());
            jsonObject1.put("CommandLine", probeModule.getCommand());
            idProbe = probeModule.getIdProbe();
            jsonArray.put(jsonObject1);
        }
        jsonObject.put("id_probe", idProbe.toString());
        jsonObject.put("listModule", jsonArray);
        return jsonObject.toJSONString();
    }
}
