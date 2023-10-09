package com.example.demo1.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TokenUtil {
    public static JSONObject deCodeToken(String token, String secretKey) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String str = decodedJWT.getClaim("login").asString();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        catch (Exception e) {
            System.out.println("Decode Lỗi rồi");
            e.printStackTrace();
        }
        return null;
    }
}
