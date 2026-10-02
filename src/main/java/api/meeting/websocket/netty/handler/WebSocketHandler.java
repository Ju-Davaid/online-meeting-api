package api.meeting.websocket.netty.handler;

import api.meeting.constant.Constant;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * WebSocket 处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelContextUtil channelContextUtil;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        JwtTokenUtils.Payload payload = channelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
        log.info("收到{}消息: {}", payload.getUsername(), msg.text());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            JwtTokenUtils.Payload payload = channelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
            if (payload == null) {
                log.warn("WebSocket 握手完成但 token 无效，关闭连接");
                ctx.writeAndFlush(new CloseWebSocketFrame(4001, "token invalid"))
                        .addListener(ChannelFutureListener.CLOSE);
                return;
            }
            channelContextUtil.addContext(payload.getUserId(), ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("有新连接加入 {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        JwtTokenUtils.Payload payload = channelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
        log.info("用户{}连接断开", payload.getUsername());
    }
}
