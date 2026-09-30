package api.meeting.websocket.netty;

import api.meeting.websocket.netty.handler.HeartBeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 启动器
 */
@Component
@Slf4j
public class NettyWebSocketStarter implements Runnable {
    @Value("${ws.port}")
    private Integer port;

    private final EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    private final EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    @Override
    public void run() {
        try {
//            ServerBootstrap serverBootstrap =
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
                            pipeline.addLast(new IdleStateHandler(0, 0, 30));
                            // 添加 HeartBeatHandler 处理心跳
                            pipeline.addLast(new HeartBeatHandler());
                        }
                    });

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
