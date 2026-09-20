package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态枚举
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    SUCCESS(200, "请求成功"),
    NOT_FOUND(404, "请求资源不存在"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限访问"),
    INTERNAL_SERVER_ERROR( 500, "服务器内部错误"),
    ILLEGAL_PARAM(400, "请求参数错误"),
    INVALID_TOKEN( 401, "无效token"),
    USER_STATUS_ERROR( 403, "用户状态异常"),
    INVALID_CAPTCHA( 400, "验证码错误"),
    INVALID_CAPTCHA_ID(400, "验证码ID错误");

    private final Integer code;
    private final String msg;
}
