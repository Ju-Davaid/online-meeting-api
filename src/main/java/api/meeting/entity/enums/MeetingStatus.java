package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会议状态枚举
 */
@Getter
@AllArgsConstructor
public enum MeetingStatus {
    NOT_STARTED(0, "未开始"),
    RUNNING(1, "进行中"),
    ENDED(2, "已结束");

    private final Integer code;
    private final String message;
}
