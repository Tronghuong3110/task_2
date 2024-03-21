package com.newlife.Connect_multiple.service;

import org.json.simple.JSONObject;

public interface IConfigService {
    JSONObject configNasAndDb(Integer serverId, Integer nasId);
}
