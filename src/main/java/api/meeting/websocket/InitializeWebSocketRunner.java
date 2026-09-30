package api.meeting.websocket;

import api.meeting.websocket.netty.NettyWebSocketStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * WebSocket初始化
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitializeWebSocketRunner implements ApplicationRunner {
    private final NettyWebSocketStarter webSocketStarter;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        new Thread(webSocketStarter).start();
    }
}
