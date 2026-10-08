package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会议会员类型枚举
 */
@Getter
@AllArgsConstructor
public enum MeetingMemberType {
    NORMAL(0, "普通会员"),
    HOST(1, "主持人");
    private final Integer code;
    private final String desc;
}
