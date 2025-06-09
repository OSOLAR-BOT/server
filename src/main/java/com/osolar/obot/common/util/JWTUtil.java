package com.osolar.obot.common.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getUserId(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", String.class);
    }

    public String createJwt(String category, String userId, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("id", userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();

    }

    public boolean isValidRefreshToken(String cookieRefresh, String redisRefresh){
        // 0. refresh 토큰 아님
        if(!getCategory(cookieRefresh).equals("refresh")){
            return false;
        }
        // 1. cookie refresh 없음
        if (cookieRefresh == null){
            return false;
        }
        // 2. cookie refresh 만료
        if (isExpired(cookieRefresh)){
            return false;
        }
        // 3. cookie refresh - redis refresh 다름
        if (!cookieRefresh.equals(redisRefresh)){
            return false;
        }
        return true;
    }

    public boolean isValidAccessToken(String accessToken){

        // 0. access 토큰 아님
        if(!getCategory(accessToken).equals("access")){
            System.out.println(" is not valid");
            return false;
        }
        // 1. access 없음
        if (accessToken == null){
            System.out.println(" is null");
            return false;
        }
        // 2. access 만료
        if (isExpired(accessToken)){
            System.out.println(" is expired");
            return false;
        }
        return true;
    }
}