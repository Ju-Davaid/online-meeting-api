package api.meeting.websocket.netty.handler;

import api.meeting.constant.Constant;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 令牌验证处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TokenVerifyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final JwtTokenUtils jwtTokenUtils;
    private final ChannelContextUtil channelContextUtil;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        log.info("收到 WebSocket 握手请求, uri={}", fullHttpRequest.uri());
        QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.uri());
        List<String> tokenList = decoder.parameters().get(Constant.WEBSOCKET_TOKEN_KEY);
        String token = (tokenList != null && !tokenList.isEmpty()) ? tokenList.get(0) : null;
        JwtTokenUtils.Payload payload = jwtTokenUtils.parseAndValidate(token);
        if (payload == null) {
            log.warn("JWT 校验失败，将在握手完成后关闭连接");
        }
        channelContextUtil.setAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY, payload);
        ctx.fireChannelRead(fullHttpRequest.retain());
    }
}
