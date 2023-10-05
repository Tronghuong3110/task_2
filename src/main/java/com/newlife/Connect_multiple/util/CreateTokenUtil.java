package com.newlife.Connect_multiple.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.json.simple.JSONObject;

import java.nio.file.StandardCopyOption;

public class CreateTokenUtil {

    public static String encodeToken(String username, String password, String topic) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("topic", topic);
        Algorithm algorithm = Algorithm.HMAC256("newlife123@");
        return JWT.create().withClaim("login", jsonObject.toJSONString())
                .sign(algorithm);
    }

    public static JSONObject deCodeToken() {
        return null;
    }
}
