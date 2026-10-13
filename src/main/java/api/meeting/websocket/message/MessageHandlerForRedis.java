package api.meeting.websocket.message;

import api.meeting.constant.Constant;
import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Redis消息处理类
 */
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = Constant.MESSAGE_QUEUE_KEY, havingValue = Constant.REDIS_MESSAGE_QUEUE)
public class MessageHandlerForRedis implements MessageHandler {
    private final RedissonClient redissonClient;
    private final ChannelContextUtil channelContextUtil;
    private final String MESSAGE_TOPIC = "message.topic";


    @Override
    public void listenMessage() {
        log.info("消息队列开始监听消息");
        RTopic topic = redissonClient.getTopic(MESSAGE_TOPIC);
        topic.addListener(MessageSendDTO.class, (MessageSendDTO, sendDTO) -> {
            log.info("收到消息: {}", JSONUtil.toJsonStr(sendDTO));
            channelContextUtil.sendMessage(sendDTO);
        });
    }

    @Override
    public <T> void sendMessage(MessageSendDTO<T> messageSendDTO) {
        RTopic topic = redissonClient.getTopic(MESSAGE_TOPIC);
        topic.publish(messageSendDTO);
    }

    @PreDestroy
    public void preDestroy() {
        redissonClient.shutdown();
    }
}
