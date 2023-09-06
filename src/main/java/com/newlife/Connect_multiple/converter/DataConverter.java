package com.newlife.Connect_multiple.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.newlife.Connect_multiple.entity.DataEntity;

import java.io.IOException;

public class DataConverter {

    public static DataEntity toEntity(JsonElement data) {
        try {
            Gson gson = new Gson();
            DataEntity dataEntity = gson.fromJson(data, DataEntity.class);
            return dataEntity;
        }
        catch (Exception e) {
            System.out.println("Convert data error");
            e.printStackTrace();
        }
        return null;
    }
}
