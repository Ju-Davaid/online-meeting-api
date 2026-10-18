package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    JOIN(1, "加入会议"),
    NORMAL(2, "普通消息"),
    LEAVE(3, "退出会议"),
    PEER(4, "信令消息");

    private final Integer code;
    private final String desc;
}
