package api.meeting.websocket.message;

import api.meeting.entity.dto.MessageSendDTO;
import org.springframework.stereotype.Component;

/**
 * 消息处理接口
 */
public interface MessageHandler {
    /**
     * 监听消息
     */
    void listenMessage();

    /**
     * 发送消息
     *
     * @param messageSendDTO 消息发送DTO
     * @param <T>            消息类型
     */
    <T> void sendMessage(MessageSendDTO<T> messageSendDTO);
}
