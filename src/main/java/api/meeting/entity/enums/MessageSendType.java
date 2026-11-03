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

    /**
     * 根据消息发送类型获取枚举
     *
     * @param messageType 消息发送类型
     * @return 消息发送类型枚举
     */
    public static MessageSendType getByCode(Integer messageType) {
        for (MessageSendType messageSendType : MessageSendType.values()) {
            if (messageSendType.getCode().equals(messageType)) {
                return messageSendType;
            }
        }
        return null;
    }
}
