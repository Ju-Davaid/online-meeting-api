package api.meeting.constant;

import api.meeting.utils.JwtTokenUtils;
import io.netty.util.AttributeKey;

public class Constant {
    /**
     * 验证码过期时间（秒）
     */
    public static final long CAPTCHA_EXPIRE_TIME = 60;
    /**
     * 用户信息会话键
     */
    public static final String USERINFO_SESSION_KEY = "userInfo";
    /**
     * token会话键
     */
    public static final String TOKEN_SESSION_KEY = "token";
    public static final String NETTY_TOKEN_PAYLOAD_KEY = "netty_token_payload";
}
