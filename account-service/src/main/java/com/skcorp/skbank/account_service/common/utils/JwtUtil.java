package com.skcorp.skbank.account_service.common.utils;

import com.skcorp.skbank.account_service.client.models.JwtPayload;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "b6ec65d4-0269-4c3d-b751-c896d06df66a";

    public String generateToken(JwtPayload jwtPayload) {
        Map<String, String> claims = new HashMap<>();
        claims.put("custId", jwtPayload.getCustId().toString());
        claims.put("lastName", jwtPayload.getLastName());
        claims.put("accountType", jwtPayload.getAccountType());
        claims.put("mobileNumber", jwtPayload.getMobileNumber());
        claims.put("branchName", jwtPayload.getBranchName());
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
