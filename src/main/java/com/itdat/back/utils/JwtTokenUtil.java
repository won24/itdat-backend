package com.itdat.back.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    // 토큰 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
//            System.out.println("토큰 유효성 검사 성공");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료됨");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 토큰 형식");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 토큰 형식");
        } catch (SignatureException e) {
            System.out.println("서명이 잘못됨");
        } catch (IllegalArgumentException e) {
            System.out.println("토큰이 비어 있거나 잘못된 값");
        }
        return false;
    }

}
