package api.meeting.websocket.netty;

import api.meeting.websocket.netty.handler.HeartBeatHandler;
import api.meeting.websocket.netty.handler.TokenVerifyHandler;
import api.meeting.websocket.netty.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 启动器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyWebSocketStarter implements Runnable {
    @Value("${ws.port}")
    private Integer port;
    private final TokenVerifyHandler tokenVerifyHandler;
    private final WebSocketHandler webSocketHandler;
    private final HeartBeatHandler heartBeatHandler;
    private final EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    @Override
    public void run() {
        try {
            Channel channel =
                    new ServerBootstrap()
                            // 设置 bossGroup 和 workerGroup
                            .group(bossGroup, workerGroup)
                            // 设置 channel 类型
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler())
                            // 设置 childHandler
                            .childHandler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(Channel ch) {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    // 添加 HttpServerCodec 处理 HTTP 请求
                                    pipeline.addLast(new HttpServerCodec());
                                    // 聚合 HttpObject 设置最大聚合长度为 64KB
                                    pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                                    // 添加 IdleStateHandler 处理空闲连接，30 秒内无数据交互则关闭连接
                                    pipeline.addLast(new IdleStateHandler(6, 0, 0));
                                    // 添加 HeartBeatHandler 处理心跳
                                    pipeline.addLast(heartBeatHandler);
                                    // 添加 TokenVerifyHandler 处理 JWT 校验
                                    pipeline.addLast(tokenVerifyHandler);
                                    // 添加 WebSocketHandler 处理 WebSocket 消息
                                    pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536, true, true));
                                    pipeline.addLast(webSocketHandler);
                                }
                            }).bind(port).sync().channel();
            log.info("Netty WebSocketStarter started on port {}", port);
            channel.closeFuture().sync();
            log.info("Netty WebSocketStarter closed on port {}", port);
        } catch (Exception e) {
            log.error("NettyWebSocketStarter run error:{}", e.getMessage(), e);
            shutdown();
        }
    }

    @PreDestroy
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
