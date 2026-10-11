package api.meeting.websocket.message;

import api.meeting.entity.dto.MessageSendDTO;
import org.springframework.stereotype.Component;

/**
 * Redis消息处理类
 */
@Component
public class MessageHandlerForRedis implements MessageHandler {
    @Override
    public void listenMessage() {

    }

    @Override
    public <T> void sendMessage(MessageSendDTO<T> messageSendDTO) {

    }
}
