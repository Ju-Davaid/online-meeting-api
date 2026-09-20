package api.meeting.utils;

import api.meeting.config.JwtConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * JWT工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final JwtConfig jwtConfig;

    @Data
    public static class Payload {
        private String userId;
        private String username;
        private LocalDateTime expiresAt;
        private Integer status;
    }

    /**
     * 生成JWT
     *
     * @param payload 载荷
     * @return JWT
     */
    public String generateToken(Payload payload) {
        log.info("generateToken: {}", JSONUtil.toJsonStr(jwtConfig));
        return JWT.create()
                .setPayload("userId", payload.getUserId())
                .setPayload("username", payload.getUsername())
                .setPayload("status", payload.getStatus())
                .setPayload("expiresAt", payload.getExpiresAt())
                .setExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .setIssuedAt(new Date())
                .setIssuer(jwtConfig.getIssuer())
                .setSigner(JWTSignerUtil.hs256(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
                .sign();
    }

    /**
     * 解析JWT
     *
     * @param token JWT
     * @return 载荷
     */
    public Payload parseToken(String token) {
        JWT jwt = JWT.of(token);
        Payload payload = new Payload();
        payload.setUserId(jwt.getPayload("userId") != null ?
                jwt.getPayload("userId").toString() : null);
        payload.setUsername((String) jwt.getPayload("username"));
        payload.setStatus(jwt.getPayload("status") != null ?
                Integer.valueOf(jwt.getPayload("status").toString()) : null);
        return payload;
    }

    /**
     * 从请求中提取JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(token)) {
            return token.replace(jwtConfig.getPrefix(), "");
        }
        return null;
    }

    /**
     * 校验JWT签名是否正确
     */
    public boolean verifyToken(String token) {
        return JWT.of(token)
                .setSigner(JWTSignerUtil.hs256(
                        jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)))
                .verify();
    }

    /**
     * 校验JWT签名 + 是否过期
     *
     * @param token      JWT
     * @param timeMillis 允许的时间偏移量（毫秒），容忍时钟偏差
     * @return 是否有效
     */
    public boolean validateToken(String token, long timeMillis) {
        try {
            JWT jwt = JWT.of(token)
                    .setSigner(JWTSignerUtil.hs256(
                            jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)));
            // verify 校验签名，validate 校验 exp / nbf / iat 时间声明
            return jwt.verify() && jwt.validate(timeMillis);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中解析并校验，一步到位
     *
     * @return 有效返回 Payload，无效返回 null
     */
    public Payload parseAndValidate(String token) {
        if (!validateToken(token, 0)) {
            return null;
        }
        return parseToken(token);
    }
}
