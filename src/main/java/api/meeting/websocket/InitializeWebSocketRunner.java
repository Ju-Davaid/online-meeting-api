package api.meeting.websocket;

import api.meeting.websocket.message.MessageHandlerForRedis;
import api.meeting.websocket.netty.NettyWebSocketStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * WebSocket初始化
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitializeWebSocketRunner implements ApplicationRunner {
    private final NettyWebSocketStarter webSocketStarter;
    private final Optional<MessageHandlerForRedis> messageHandlerForRedis;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        new Thread(webSocketStarter, "websocket-starter").start();
        messageHandlerForRedis.ifPresent(handlerForRedis -> new Thread(handlerForRedis::listenMessage, "message-handler").start());
    }
}
