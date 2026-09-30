package api.meeting.websocket.netty.handler;

import api.meeting.constant.Constant;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.utils.ChannelContextUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳处理类
 */
@Slf4j
public class HeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            switch (state) {
                case READER_IDLE -> {
                    JwtTokenUtils.Payload payload = ChannelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
                    log.info("用户{}心跳超时", JSONUtil.toJsonStr(payload));
                    ctx.close();
                }
                case WRITER_IDLE -> ctx.writeAndFlush("hear");
            }
        }
    }
}
