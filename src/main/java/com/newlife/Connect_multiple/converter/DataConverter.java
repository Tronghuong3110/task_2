package com.newlife.Connect_multiple.converter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
