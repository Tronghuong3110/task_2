package com.newlife.Connect_multiple.api;

import com.newlife.Connect_multiple.entity.SubtopicServerEntity;
import com.newlife.Connect_multiple.util.ConstVariable;
import com.squareup.okhttp.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiAddInfoToBroker {
    private static final String usernameBroker = ConstVariable.username;
    private static final String passwordBroker = ConstVariable.password;
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
    public static String addRuleToBroker(String username, List<SubtopicServerEntity> listSubTopic) {
        try {
            // xoá toàn bộ role của user trước khi thêm role mới cho user
            Boolean deleteRoleOfUser = deleteRuleOfUser(username);
            // xóa role không thành công
            if(!deleteRoleOfUser) {
                return null;
            }
            MediaType type = MediaType.parse("application/json; charset=utf-8");
            String jsonData = createRule(username, listSubTopic);
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
    private static String createRule(String username, List<SubtopicServerEntity> listTopic) {
        // json chứa danh sách các rules và username cần thêm rule
        JSONObject dataObject = new JSONObject();
        dataObject.put("username", username);
        //chứa danh sách các rule
        JSONArray rulesArray = new JSONArray();

        // thêm rule vào danh sách
        for(SubtopicServerEntity topic : listTopic){
            JSONObject rule = new JSONObject();
            rule.put("action", "all");
            rule.put("permission", "allow");
            rule.put("topic", topic.getSubTopic());
            rulesArray.add(rule);
        }
        dataObject.put("rules", rulesArray);

        String jsonString = dataObject.toJSONString();
        return jsonString;
    }
    private static Boolean deleteRuleOfUser(String username) {
        try {
            MediaType type = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlApiAddRule + username)
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(usernameBroker, passwordBroker))
                    .delete()
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            Integer responseCode = response.code();
            // Th user chưa được cập quyền hoặc đã xóa quyền của user thành công
            if(responseCode == 404 || responseCode == 204) {
                return true;
            }
            return false;
        }
        catch (Exception e) {
            System.out.print("Xóa quyền của user không thành công!(Line 105)");
            e.printStackTrace();
            return false;
        }
    }
}
