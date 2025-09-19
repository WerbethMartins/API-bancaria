package com.banco.api_bancaria.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMs;

    private SecretKey getSignKey(){
        logger.info("Gerando chave de assinatura com secret: {}", jwtSecret);
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        try {
            String username = userDetails.getUsername();
            logger.info("Gerando token para username: {}", username);
            if (username == null) {
                throw new IllegalArgumentException("Username não pode ser nulo!");
            }

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            logger.info("Roles incluídas no token: {}", roles);

            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Token gerado com sucesso: {}", token);
            return token;
        } catch (Exception e) {
            logger.error("Erro ao gerar token", e);
            throw new RuntimeException("Erro ao gerar token JWT: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            // Token inválido ou expirado
            return false;
        }
    }

    public String generateRefreshToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
