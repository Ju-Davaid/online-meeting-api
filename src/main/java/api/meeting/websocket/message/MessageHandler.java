package api.meeting.websocket.message;

import api.meeting.entity.dto.MessageSendDTO;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 消息处理接口
 */
public interface MessageHandler {
    /**
     * 监听消息
     */
    default void listenMessage() {
    }

    /**
     * 监听消息
     *
     * @param channel RabbitMQ通道
     */
    default void listenMessage(Message message, Channel channel) throws IOException {
    }

    ;

    /**
     * 发送消息
     *
     * @param messageSendDTO 消息发送DTO
     * @param <T>            消息类型
     */
    <T> void sendMessage(MessageSendDTO<T> messageSendDTO);

    void set();
}
