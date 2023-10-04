package com.newlife.Connect_multiple.api;
import com.google.gson.JsonArray;
import com.newlife.Connect_multiple.util.JsonUtil;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

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
            JSONObject jsonObject = JsonUtil.parseJson(responseBody);
            try{
                Object data = jsonObject.get("connected");
                if(data.equals("true"))
                    return true;
            }
            catch (Exception e){
                Object messageCode = jsonObject.get("code");
                if(messageCode.equals("CLIENTID_NOT_FOUND")) {
                    return false;
                }
            }
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
