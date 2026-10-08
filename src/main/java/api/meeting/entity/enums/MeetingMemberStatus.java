package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会议会员状态枚举
 */
@Getter
@AllArgsConstructor
public enum MeetingMemberStatus {
    NORMAL(0, "正常会员"),
    DELETE(1, "已删除"),
    EXIT(2, "已退出"),
    KICKED(3, "已被踢出"),
    BLACKLISTED(4, "已被拉黑");

    private final Integer code;
    private final String desc;
}
