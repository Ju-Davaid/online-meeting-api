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
    private static final String CAPTCHA_KEY = "captcha:%s";
    /**
     * 黑名单token缓存键
     * 格式：blacklist:token:userId
     */
    private static final String BLACKLIST_TOKEN = "blacklist:token:%s";

    /**
     * 获取验证码缓存键
     *
     * @param captchaId 验证码ID
     * @return 验证码缓存键
     */
    public static String getCaptchaKey(String captchaId) {
        return String.format(CAPTCHA_KEY, captchaId);
    }

    /**
     * 获取黑名单token缓存键
     *
     * @param userId 用户ID
     * @return 黑名单token缓存键
     */
    public static String getBlacklistTokenKey(String userId) {
        return String.format(BLACKLIST_TOKEN, userId);
    }
}
