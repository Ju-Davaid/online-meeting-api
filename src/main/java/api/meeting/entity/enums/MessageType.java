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
    PEER(4, "信令消息"),
    END(5, "结束会议"),
    FRIEND_APPLY(6, "好友申请"),
    HANDLE_APPLY(7, "处理好友申请"),
    INVITE(8, "邀请入会"),
    VIDEO_STATUS_CHANGE(9, "视频状态变更");


    private final Integer code;
    private final String desc;
}
