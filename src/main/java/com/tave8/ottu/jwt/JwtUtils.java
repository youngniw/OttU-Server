package com.tave8.ottu.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final String secretKey;
    private Key key;

    public JwtUtils(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
    }

    public String makeJwtToken(String email) {
        String jwt = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("ottu")
                .setIssuedAt(new Date())
                //.setExpiration(new Date(now.getTime() + Duration.ofDays(30).toMillis()))
                .claim("email", email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            log.info("이미 탈퇴한 회원입니다.");
        }
        return false;
    }
}
