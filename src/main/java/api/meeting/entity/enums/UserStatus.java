package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用");
    private final Integer code;
    private final String desc;
}
