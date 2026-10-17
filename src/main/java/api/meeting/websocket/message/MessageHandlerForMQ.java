package api.meeting.websocket.message;

import api.meeting.constant.Constant;
import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * RabbitMQ消息处理类
 */
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = Constant.MESSAGE_QUEUE_KEY, havingValue = Constant.RABBITMQ_MESSAGE_QUEUE)
public class MessageHandlerForMQ implements MessageHandler {
    private final RabbitTemplate rabbitTemplate;
    private final ChannelContextUtil channelContextUtil;
    private final String MESSAGE_QUEUE = "message-queue";
    private final String MESSAGE_EXCHANGE = "message-exchange";


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MESSAGE_QUEUE),
                    exchange = @Exchange(
                            name = MESSAGE_EXCHANGE,
                            type = ExchangeTypes.FANOUT
                    )
            )
    )
    @Override
    public void listenMessage(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("收到消息：{}", messageBody);
            channelContextUtil.sendMessage(JSONUtil.toBean(messageBody, MessageSendDTO.class));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("消费消息异常", e);
            handleFailMessage(channel, message);
        }
    }

    @Override
    public <T> void sendMessage(MessageSendDTO<T> messageSendDTO) {
        rabbitTemplate.convertAndSend(MESSAGE_EXCHANGE, MESSAGE_QUEUE, messageSendDTO);
        log.info("发送消息到 RabbitMQ: {}", messageSendDTO);
    }

    /**
     * 处理失败消息
     *
     * @param channel 通道
     * @param message 消息
     * @throws IOException IOException
     */
    private void handleFailMessage(Channel channel, Message message) throws IOException {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        int retryCount = (int) headers.computeIfAbsent(Constant.MESSAGE_RETRY_COUNT_KEY, k -> 0);
        if (retryCount < Constant.MAX_MESSAGE_RETRY_COUNT - 1) {
            headers.put(Constant.MESSAGE_RETRY_COUNT_KEY, retryCount + 1);
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder().headers(headers).build();
            channel.basicPublish("", MESSAGE_QUEUE, basicProperties, message.getBody());
            channel.basicAck(deliveryTag, false);
        } else {
            channel.basicReject(deliveryTag, false);
            log.error("消息重试次数超过最大重试次数，消息已拒绝");
        }
    }
}
