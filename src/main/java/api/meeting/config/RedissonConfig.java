package api.meeting.config;

import api.meeting.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = Constant.MESSAGE_QUEUE_KEY, havingValue = Constant.REDIS_MESSAGE_QUEUE)
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private Integer port;
    @Value("${spring.data.redis.password}")
    private String password;

    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            config.setPassword(password);
            config.useSingleServer()
                    .setAddress("redis://" + host + ":" + port);
            return Redisson.create(config);
        } catch (Exception e) {
            log.error("RedissonClient 初始化失败", e);
            return null;
        }
    }
}
