package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息发送类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageSendType {
    USER(0, "个人"),
    GROUP(1, "群组");

    private final Integer code;
    private final String desc;

    public static boolean isUser(Integer messageType) {
        return USER.getCode().equals(messageType);
    }
}
