package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会议编号类型枚举
 */
@Getter
@AllArgsConstructor
public enum MeetingNoType {
    AUTO(0, "自动编号"),
    CUSTOM(1, "自定义编号");

    private final Integer code;
    private final String desc;

    /**
     * 是否自动编号
     */
    public static boolean isAuto(Integer code) {
        return AUTO.code.equals(code);
    }
}
