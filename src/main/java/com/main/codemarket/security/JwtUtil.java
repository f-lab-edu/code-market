package com.main.codemarket.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyRaw;

    @Value("${jwt.token-expire-time}")
    private long expireTime;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        // 안전한 키 생성 (256비트 이상)
        SECRET_KEY = Keys.hmacShaKeyFor(secretKeyRaw.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String createToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    //토큰에서 구분값 추출
    public String getSubject(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    //토큰 유효한지 검사
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("토큰이 만료되었습니다");
        } catch (SignatureException | MalformedJwtException e) {
            throw new IllegalArgumentException("토큰 서명/형식 오류");
        }
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("요청 헤더의 형식이 잘못되었습니다");
        }

        String[] headers = header.split(" ");
        if (headers.length < 1) {
            throw new IllegalArgumentException("토큰의 형식이 잘못되었습니다");
        }
        return headers[1];
    }

}

