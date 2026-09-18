package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态枚举
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS((short) 200, "请求成功"),
    NOT_FOUND((short) 404, "请求资源不存在"),
    UNAUTHORIZED((short) 401, "未登录或登录已过期"),
    FORBIDDEN((short) 403, "没有权限访问"),
    INTERNAL_SERVER_ERROR((short) 500, "服务器内部错误"),
    ILLEGAL_PARAM((short) 400, "请求参数错误"),
    INVALID_TOKEN((short) 401, "无效token"),
    USER_STATUS_ERROR((short) 403, "用户状态异常"),
    INVALID_CAPTCHA((short) 400, "验证码错误"),
    INVALID_CAPTCHA_ID((short) 400, "验证码ID错误");

    private final Short code;
    private final String msg;
}
