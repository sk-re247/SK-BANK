package com.skcorp.skbank.card_service.common.utils;

import com.skcorp.skbank.card_service.client.models.JwtPayload;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "f3f7b1d7a1c687db5cfbbbf5a618f967b88bfa9efc6d17884eabef74d86a452f";

    public String generateToken(JwtPayload jwtPayload) {
        Map<String, String> claims = new HashMap<>();
        claims.put("custId", jwtPayload.getCustId().toString());
        claims.put("lastName", jwtPayload.getLastName());
        claims.put("accountType", jwtPayload.getAccountType());
        claims.put("mobileNumber", jwtPayload.getMobileNumber());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(jwtPayload.getAccountNumber())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractAccountNumber(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
}