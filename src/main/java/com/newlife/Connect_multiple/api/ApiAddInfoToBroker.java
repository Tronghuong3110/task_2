package com.newlife.Connect_multiple.api;

import com.newlife.Connect_multiple.dto.DataObject;
import com.newlife.Connect_multiple.dto.ProbeDto;
import com.newlife.Connect_multiple.dto.Rules;
import com.newlife.Connect_multiple.entity.ProbeEntity;
import com.newlife.Connect_multiple.entity.ProbeOptionEntity;
import com.newlife.Connect_multiple.entity.ServerEntity;
import com.squareup.okhttp.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiAddInfoToBroker {

    private static final String usernameBroker = "2aee9b12c90aabd6";
    private static final String passwordBroker = "iyOKvKD2t7uw2LUM7dOMcIYDYN4Bg9AuX8ZahoKRBwvM";
    private static String urlApiCreateUser = "http://localhost:18083/api/v5/authentication/password_based%3Abuilt_in_database/users";
    private static String urlApiAddRule = "http://localhost:18083/api/v5/authorization/sources/built_in_database/rules/users/";
    // add user to broker
    public static String addUserToBroker(String username, String password) {
        try {
            MediaType type = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", username);
            jsonObject.put("password", password);
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(type, jsonObject.toJSONString());
            Request request = new Request.Builder()
                    .url(urlApiCreateUser)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", Credentials.basic(usernameBroker, passwordBroker))
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            Integer responseCode = response.code();
            if(responseCode == 201) {
                System.out.println(response.toString());
                return "Create user success";
            }
            return response.toString();
        }
        catch (IOException e) {
            System.out.println("Lỗi rồi");
            e.printStackTrace();
        }
        return null;
    }
    public static String addRuleToBroker(String username, String topic) {
        try {
            MediaType type = MediaType.parse("application/json; charset=utf-8");
            String jsonData = createRule(username, topic);
            System.out.println(jsonData);
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(type, jsonData);
            Request request = new Request.Builder()
                    .url(urlApiAddRule + username)
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(usernameBroker, passwordBroker))
                    .put(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            Integer responseCode = response.code();
            if(responseCode == 204) { // add rule thành công
                System.out.println(response);
                return "Create rule success";
            }
            return response.toString();
        }
        catch (IOException e) {
            System.out.println("Lỗi rồi");
            e.printStackTrace();
        }
        return null;
    }
    private static String createRule(String username, String topic) {
        JSONObject dataObject = new JSONObject();
        dataObject.put("username", username);

        JSONArray rulesArray = new JSONArray();
        // danh sách rule
        JSONObject rule = new JSONObject();
        rule.put("action", "all");
        rule.put("permission", "allow");
        rule.put("topic", topic);
        rulesArray.add(rule);
        dataObject.put("rules", rulesArray);

        String jsonString = dataObject.toJSONString();
        return jsonString;
    }
}
