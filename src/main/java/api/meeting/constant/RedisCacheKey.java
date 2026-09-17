package api.meeting.constant;

/**
 * Redis缓存键常量类
 * 用于定义Redis缓存键的常量
 */
public class RedisCacheKey {
    /**
     * 验证码缓存键
     * 格式：captcha:验证码id
     */
    public static final String CAPTCHA_KEY = "captcha:%s";
}
