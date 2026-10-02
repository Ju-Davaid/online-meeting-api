package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    USER(0, "个人"),
    SYSTEM(1, "群组");

    private final Integer code;
    private final String desc;

    public static boolean isUser(Integer messageType) {
        return USER.getCode().equals(messageType);
    }
}
