package api.meeting.constant;

import api.meeting.utils.JwtTokenUtils;
import io.netty.util.AttributeKey;

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
}
