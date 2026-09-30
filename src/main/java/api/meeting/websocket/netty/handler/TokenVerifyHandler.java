package api.meeting.websocket.netty.handler;

import api.meeting.config.JwtConfig;
import api.meeting.constant.Constant;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.utils.ResponseUtils;
import api.meeting.websocket.utils.ChannelContextUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 令牌验证处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TokenVerifyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final JwtConfig jwtConfig;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        String token = fullHttpRequest.headers().get(jwtConfig.getHeader());
        if (StringUtils.hasText(token)) {
            token = token.replace(jwtConfig.getPrefix(), "");
        }
        JwtTokenUtils.Payload payload = jwtTokenUtils.parseAndValidate(token);
        if (payload == null) {
            log.error("JWT 校验失败:{}", token);
            ResponseUtils.writeNettyResponse(ctx, ResponseCode.FORBIDDEN);
            return;
        }
        ChannelContextUtil.<JwtTokenUtils.Payload>setAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY, payload);
        ctx.fireChannelRead(fullHttpRequest.retain());
        // TODO token 校验通过，继续处理后续消息
    }
}
