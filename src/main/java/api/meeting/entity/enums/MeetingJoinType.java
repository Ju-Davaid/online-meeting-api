package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 会议加入类型枚举
 */
@Getter
@AllArgsConstructor
public enum MeetingJoinType {
    NO_PASSWORD(0, "无密码加入"),
    PASSWORD(1, "密码加入");
    private final Integer code;
    private final String desc;
}
