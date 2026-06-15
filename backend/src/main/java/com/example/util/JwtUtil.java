package com.example.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.enums.ResultCode;
import com.example.exception.BusinessException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // 訪問令牌過期時間（毫秒）

    @Value("${jwt.refresh-expiration:604800000}") // 默認7天
    private Long refreshExpiration;

    // 緩存密鑰對象，避免重複創建
    private SecretKey cachedSigningKey;

    /**
     * 獲取簽名密鑰
     */
    private SecretKey getSigningKey() {
        if (cachedSigningKey == null) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

            // 確保密鑰長度足夠（HS256需要至少256 bits = 32 bytes）
            if (keyBytes.length < 32) {
                log.warn("JWT密鑰長度不足32字節，建議使用更長的密鑰以提高安全性");
                // 填充密鑰到32字節（僅用於開發環境）
                byte[] paddedKeyBytes = new byte[32];
                System.arraycopy(keyBytes, 0, paddedKeyBytes, 0, Math.min(keyBytes.length, 32));
                keyBytes = paddedKeyBytes;
            }

            cachedSigningKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedSigningKey;
    }

    /**
     * 從token中提取用戶名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 從token中提取過期時間
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 從token中提取角色
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> (Set<String>) claims.get("roles"));
    }

    /**
     * 提取指定的claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析token的所有claims（使用新API）
     */
    private Claims extractAllClaims(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式錯誤: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("JWT簽名驗證失敗: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌參數非法: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 檢查token是否過期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 生成訪問令牌
     */
    public String generateToken(String username, Set<String> roles) {
        return generateToken(username, roles, expiration);
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username, Set<String> roles) {
        return generateToken(username, roles, refreshExpiration);
    }

    /**
     * 生成令牌（通用方法）
     */
    private String generateToken(String username, Set<String> roles, Long tokenExpiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("username", username);
        claims.put("token_type", tokenExpiration.equals(this.expiration) ? "access" : "refresh");

        return createToken(claims, username, tokenExpiration);
    }

    /**
     * 創建token（使用新API）
     */
    private String createToken(Map<String, Object> claims, String subject, Long tokenExpiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 驗證token是否有效（完整驗證）
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            return isValid;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 簡單的token驗證（僅檢查格式和簽名）
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            boolean notExpired = !isTokenExpired(token);
            return notExpired;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 獲取令牌剩餘有效時間（毫秒）
     */
    public Long getRemainingValidity(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(remaining, 0);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 檢查令牌是否需要刷新（剩餘時間少於1/3）
     */
    public Boolean shouldRefreshToken(String token) {
        Long remaining = getRemainingValidity(token);
        return remaining > 0 && remaining < (expiration / 3);
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            throw new JwtException("無效的令牌，無法刷新");
        }

        String username = extractUsername(token);
        Set<String> roles = extractRoles(token);

        // 檢查是否為刷新令牌
        String tokenType = extractClaim(token, claims -> claims.get("token_type", String.class));
        if ("refresh".equals(tokenType)) {
            log.info("使用刷新令牌生成新的訪問令牌: {}", username);
            return generateToken(username, roles); // 生成新的訪問令牌
        }

        // 如果是訪問令牌，直接刷新
        return generateToken(username, roles);
    }

    /**
     * 從令牌中獲取所有信息
     */
    public Map<String, Object> getTokenInfo(String token) {
        Map<String, Object> info = new HashMap<>();
        try {
            Claims claims = extractAllClaims(token);
            info.put("username", claims.getSubject());
            info.put("roles", claims.get("roles"));
            info.put("issuedAt", claims.getIssuedAt());
            info.put("expiration", claims.getExpiration());
            info.put("remainingValidity", getRemainingValidity(token));
            info.put("valid", true);
        } catch (Exception e) {
            info.put("valid", false);
            info.put("error", e.getMessage());
        }
        return info;
    }
}