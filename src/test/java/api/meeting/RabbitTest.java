package api.meeting;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitTest {
    @Resource
    private RabbitTemplate rabbitTemplate;
    private final static String QUEUE_NAME = "test.queue";

    @Test
    public void testSimpleSend() {
        rabbitTemplate.convertAndSend(QUEUE_NAME, "test");
    }

    @Test
    public void testSimpleReceive() {
        Object msg = rabbitTemplate.receiveAndConvert("queueName", 3000);
        System.out.println("收到消息:"+msg);
    }
}
