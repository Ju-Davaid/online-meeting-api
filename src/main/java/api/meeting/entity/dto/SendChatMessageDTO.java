package api.meeting.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送聊天消息DTO
 */
@Data
public class SendChatMessageDTO {
    private String message;
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
    private String receiveUserId;
    private String fileName;
    private Long fileSize;
    private Integer fileType;
}
