package com.itheima.recorddid.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.nio.charset.StandardCharsets; // 记得导入这个
@Component
public class JwtUtil {
    // 密钥（项目上线更换复杂字符串，至少32位）
    private static final String SECRET_KEY_STR = "recorddidsecretkey1234567890abcdefghijklmn";
    // 令牌有效期 24小时（毫秒）
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // 获取加密密钥
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STR.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * @param userId 用户id
     * @param role 用户角色 buyer/seller
     * @return token字符串
     */
    public static String generateToken(Long userId, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析令牌，获取载荷
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}