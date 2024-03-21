package com.newlife.Connect_multiple.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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

    public static String deCodePass(String pass) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("newlife123@");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(pass);
            String str = decodedJWT.getClaim("login").asString();
            return str;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Decode error!!!");
            return null;
        }
    }

    public static String enCodePass(String pass) {
        Algorithm algorithm = Algorithm.HMAC256("newlife123@");
        return JWT.create().withClaim("login", pass).sign(algorithm);
    }
}
