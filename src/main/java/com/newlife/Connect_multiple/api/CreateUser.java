package com.newlife.Connect_multiple.api;

import com.squareup.okhttp.*;

import java.io.IOException;

public class CreateUser {

    private static final String username = "2aee9b12c90aabd6";
    private static final String password = "iyOKvKD2t7uw2LUM7dOMcIYDYN4Bg9AuX8ZahoKRBwvM";
    private static String urlApi = "http://localhost:18083/api/v5/authentication/1/users";

    public static void main(String[] args) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody body = new FormEncodingBuilder()
                    .add("user_id", "tronghuong")
                    .add("password", "1234")
                    .build();
            Request request = new Request.Builder()
                    .url(urlApi)
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(username, password))
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            Integer responseCode = response.code();
            if(responseCode == 200) {
                System.out.println(response.toString());
            }
        }
        catch (IOException e) {
            System.out.println("Lỗi rồi");
            e.printStackTrace();
        }
    }
}
