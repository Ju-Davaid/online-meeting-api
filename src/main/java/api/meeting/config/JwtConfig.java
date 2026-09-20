package api.meeting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置类
 */
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;
    private String header;
    private String prefix;
    private String issuer;
}
