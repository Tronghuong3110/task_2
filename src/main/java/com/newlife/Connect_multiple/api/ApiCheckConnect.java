package com.newlife.Connect_multiple.api;
import com.google.gson.JsonArray;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

public class ApiCheckConnect {
    private static final String username = "2aee9b12c90aabd6";
    private static final String password = "iyOKvKD2t7uw2LUM7dOMcIYDYN4Bg9AuX8ZahoKRBwvM";
    public static Boolean checkExistClient(String clientId) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:18083/api/v5/clients/"+clientId)
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(username, password))
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
//            System.out.print(response.body());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody,JsonObject.class);
            try{
                JsonObject meta = jsonObject.getAsJsonObject("meta");
                JsonArray data = jsonObject.getAsJsonArray("data");
//                for(int i = 0; i < data.size(); i++) {
////                    Những client còn connect tới
////                    DataEntity dataEntity = DataConverter.toEntity(data.get(i));
////                    System.out.println(dataEntity.getClientid());
//                }
                int count = meta.get("count").getAsInt();
                return true;
            }
            catch (Exception e){
                String messageCode = jsonObject.get("code").getAsString();
                if(messageCode.equals("CLIENTID_NOT_FOUND")) {
                    return false;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


//    public static void main(String[] args) {
//        System.out.println(checkExistClient(""));
//    }
}

//            OkHttpClient client = new OkHttpClient();
//
//            Request request = new Request.Builder()
//                    .url("http://localhost:18083/api/v5/clients/"+clientId)
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", Credentials.basic(this.username, this.password))
//                    .build();
//            Response response = client.newCall(request).execute();

//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(responseBody,JsonObject.class);
//        try{
//            String messageCode = jsonObject.get("code").getAsString();
//            return messageCode;
//        }
//        catch (Exception e){
//            JsonObject meta = jsonObject.getAsJsonObject("meta");
//            int count = meta.get("count").getAsInt();
//            return count+"";
//        }
