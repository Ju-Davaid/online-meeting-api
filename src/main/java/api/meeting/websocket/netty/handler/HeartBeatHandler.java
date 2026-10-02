package api.meeting.websocket.netty.handler;

import api.meeting.constant.Constant;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 心跳处理类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HeartBeatHandler extends ChannelDuplexHandler {
    private final ChannelContextUtil channelContextUtil;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            switch (state) {
                case READER_IDLE -> {
                    JwtTokenUtils.Payload payload = channelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
                    log.info("用户{}心跳超时", payload.getUsername());
                    ctx.close();
                }
                case WRITER_IDLE -> ctx.writeAndFlush("hear");
            }
        }
    }
}
