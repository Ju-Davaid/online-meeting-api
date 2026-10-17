package api.meeting.constant;

/**
 * 常量类
 */
public class Constant {
    /**
     * 验证码过期时间（秒）
     */
    public static final long CAPTCHA_EXPIRE_TIME = 60;
    /**
     * netty token payload 会话键
     */
    public static final String NETTY_TOKEN_PAYLOAD_KEY = "netty_token_payload";
    /**
     * websocket token 会话键
     */
    public static final String WEBSOCKET_TOKEN_KEY = "token";
    /**
     * 消息队列键
     */
    public static final String MESSAGE_QUEUE_KEY = "message.queue";
    /**
     * redis 消息队列键
     */
    public static final String REDIS_MESSAGE_QUEUE = "redis";
    /**
     * rabbitmq 消息队列键
     */
    public static final String RABBITMQ_MESSAGE_QUEUE = "rabbitmq";
    /**
     * 消息重试次数键
     */
    public static final String MESSAGE_RETRY_COUNT_KEY = "message-retry-count";
    /**
     * 最大消息重试次数
     */
    public static final Integer MAX_MESSAGE_RETRY_COUNT = 3;

}
