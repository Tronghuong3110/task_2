package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.entity.ProbeModuleEntity;
import com.newlife.Connect_multiple.util.JsonUtil;
import org.json.simple.JSONObject;

import java.util.Optional;

public class Test {

    public static void main(String[] args) {
        ProbeModuleEntity probeModuleEntity = new ProbeModuleEntity();
        probeModuleEntity.setIdProbe(1);
        probeModuleEntity.setId(5);
        probeModuleEntity.setPath("path");
        probeModuleEntity.setArg("125");
        probeModuleEntity.setPathLog("path log");
        probeModuleEntity.setIdModule(1);
        probeModuleEntity.setCommand("command line");
        probeModuleEntity.setCaption("ping");
        probeModuleEntity.setModuleName("test");
        String jsonStr = JsonUtil.createJson(probeModuleEntity, "123", Optional.ofNullable(null), Optional.ofNullable(null));
        JSONObject jsonObject = JsonUtil.parseJson(jsonStr);
        System.out.println(jsonObject.get("path"));
    }
}
