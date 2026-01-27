package com.example.mealTracker.common;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;



public class JwtProvider {
        private final Key key;
        private final long accessMinutes;
        private final long refreshDays;

    public JwtProvider(String secret, long accessMinutes, long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }


    public String createAccessToken(long userId, String email) {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(accessMinutes * 60);
            return Jwts.builder()
                    .subject(String.valueOf(userId))
                    .claims(Map.of("email", email, "typ", "access"))
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .signWith(key)
                    .compact();
    }

    public String createRefreshToken(long userId) {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(refreshDays * 24 * 60 * 60);
            return Jwts.builder()
                    .subject(String.valueOf(userId))
                    .claims(Map.of("typ", "refresh"))
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(exp))
                    .signWith(key)
                    .compact();
    }

    //만료된 토큰 만들기
    public String createExpiredAccessToken(long userId, String email) {
        Instant now = Instant.now();
        Instant issuedAt = now.minusSeconds(60);
        Instant expiredAt = now.minusSeconds(30);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("email", email, "typ", "access"))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiredAt))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
            return Jwts.parser().verifyWith((javax.crypto.SecretKey) key).build().parseSignedClaims(token);
    }

    public boolean isType(String token, String type) {
            try {
                    Claims c = parseToken(token).getPayload();
                    return type.equals(c.get("typ",String.class));
            } catch (Exception e) {
                    return false;
            }
    }

    public long getUserId(String token) {
            return Long.parseLong(parseToken(token).getPayload().getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

}
