package com.newlife.Connect_multiple.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.newlife.Connect_multiple.util.JsonUtil;
import org.json.simple.JSONObject;
public class Test1 {

    private static String deCodeToken(String token, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String str = decodedJWT.getClaim("data").asString();
        JSONObject jsonObject = JsonUtil.parseJson(str);
        String username = (String) jsonObject.get("username");
        System.out.println(username);
        return null;
    }
    private static String enCode(String jsonObject, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create().withClaim("data", jsonObject)
                .sign(algorithm);
    }
    public static void main(String[] args) {
        String secretKey = "tronghuong";
        String jsonData = "{"
                + "\"broker\": \"1234\", "
                + "\"clientId\": \"client1\", "
                + "\"username\": \"tronghuong\", "
                + "\"password\": \"1234\", "
                + "\"connectTimeOut\": 5, "
                + "\"CleanSession\": true, "
                + "\"subtopic\": \"server\", "
                + "\"pubtopic\": \"client1\""
                + "}";

        String token = enCode(jsonData, secretKey);
        System.out.println(token);
        String decodedJWT = deCodeToken(token, secretKey);
    }
}
