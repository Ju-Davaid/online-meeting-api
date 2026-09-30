package api.meeting.websocket.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * WebSocket 处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        log.info("收到消息: {}", msg.text());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        log.info("有新连接加入 {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
       log.info("有连接断开 {}", ctx.channel());
    }
}
