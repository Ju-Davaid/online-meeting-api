package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举类
 */
@Getter
@AllArgsConstructor
public enum MessageStatus {
    SENDING(1, "发送中"),
    SUCCESS(2, "发送成功"),
    FAIL(3, "发送失败");

    private final Integer code;
    private final String desc;
}
