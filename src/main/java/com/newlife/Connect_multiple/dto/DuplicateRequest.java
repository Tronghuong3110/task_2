package com.newlife.Connect_multiple.dto;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

@Data
public class DuplicateRequest {
    private List<JSONObject> listProbe;
    private Integer probeOrigin;
}
