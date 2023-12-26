package com.newlife.Connect_multiple.util;

import com.newlife.Connect_multiple.entity.ProbeEntity;
import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Optional;

public class JsonUtil {
    public static String createJson(ProbeModuleEntity probeModuleEntity, String idCmdHistory,
                                    Optional<String> cmdWin, Optional<String> cmdLinux, String action, String probeName) {
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
            jsonObject.put("probeName", probeName);
            jsonObject.put("typeModule", probeModuleEntity.getTypeModuleName());
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
            Integer code = codeResponse(status);
            JSONObject res = new JSONObject();
            res.put("message", message);
            res.put("status", status);
            res.put("code", code);
            return res;
        }
        catch (Exception e) {
            System.out.println("Create response to front error");
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonStatus(String action, List<ProbeModuleEntity> probeModuleEntities, String probeName, Integer idProbe, Boolean pending) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        JSONArray jsonArray = new JSONArray();
        for (ProbeModuleEntity probeModule : probeModuleEntities) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id_probe_module", probeModule.getId().toString());
            jsonObject1.put("path_log", probeModule.getPathLog());
            jsonObject1.put("PID", probeModule.getProcessId());
            jsonObject1.put("cmd_win", probeModule.getCommand());
            jsonObject1.put("cmd_linux", probeModule.getCommand());
            jsonObject1.put("CommandLine", probeModule.getCommand());
            jsonObject1.put("pending", pending);
            jsonArray.put(jsonObject1);
        }
        jsonObject.put("id_probe", idProbe.toString());
        jsonObject.put("probeName", probeName);
        jsonObject.put("listModule", jsonArray);
        return jsonObject.toJSONString();
    }

    public static String createJsonGetCpu(String action, Integer idProbe) {
        try {
            JSONObject json = new JSONObject();
            json.put("action", action);
            json.put("id_probe", idProbe);
            return json.toJSONString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Integer codeResponse(String status) {
        switch (status) {
            case "Running":
                return 1;
            case "Failed":
                return 3;
            case "Pending":
                return 4;
            case "Stopped":
                return 2;
        }
        return 0;
    }
}
