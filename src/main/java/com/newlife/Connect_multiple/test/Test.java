package com.newlife.Connect_multiple.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Test {
    public static void main(String[] args) {
        JSONObject jsonObject = deCodeToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbiI6IntcInBhc3N3b3JkXCI6XCIxMjM0XCIsXCJ0b3BpY1wiOlwiY2xpZW50XzEyMzQ1Njc4OVwiLFwidXNlcm5hbWVcIjpcInNlcnZlclwifSJ9.PKuvmDEcyoxCRin73PA1SiUnUax9-ZGN5eAXsh3Gv7Y", "newlife123@");
        System.out.println("Username "+ jsonObject.get("username"));
        System.out.println("Password " + jsonObject.get("password"));
        System.out.println("Topic " + jsonObject.get("topic"));
    }

    private static JSONObject deCodeToken(String token, String secretKey) {
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
