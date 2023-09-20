package com.newlife.Connect_multiple.util;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import org.json.simple.JSONObject;

import java.util.Optional;

public class JsonUtil {
    public static String createJson(ProbeModuleEntity probeModuleEntity, String idCmdHistory, Optional<String> cmdWin, Optional<String> cmdLinux) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idProbeModule", probeModuleEntity.getId());
        jsonObject.put("idCmdHistory", idCmdHistory);
        jsonObject.put("path", probeModuleEntity.getPath());
        jsonObject.put("caption", probeModuleEntity.getCaption());
        jsonObject.put("arg", probeModuleEntity.getArg());
        jsonObject.put("cmd_win", cmdWin.orElse(probeModuleEntity.getCommand()));
        jsonObject.put("cmd_linux", cmdLinux.orElse(probeModuleEntity.getCommand()));
        jsonObject.put("pathLog", probeModuleEntity.getPathLog());
        return jsonObject.toJSONString();
    }
}
