package com.newlife.Connect_multiple.api;
import com.newlife.Connect_multiple.util.ConstVariable;
import com.newlife.Connect_multiple.util.JsonUtil;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.simple.JSONObject;

import java.io.IOException;

public class ApiCheckConnect {
    private static final String username = ConstVariable.KEY_BROKER;
    private static final String password = ConstVariable.SECRET_KEY_BROKER;

    public static Boolean checkExistClient(String clientId) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:18083/api/v5/clients/" + clientId)
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(username, password))
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = JsonUtil.parseJson(responseBody);
            try {
                Object data = jsonObject.get("connected");
                System.out.println("Kết Quả " + data);
                if (data.equals(true)) {
                    System.out.println("Kết quả cuối cùng " + data);
                    return true;
                }
            } catch (Exception e) {
                Object messageCode = jsonObject.get("code");
                if (messageCode.equals("CLIENTID_NOT_FOUND")) {
                    return false;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}