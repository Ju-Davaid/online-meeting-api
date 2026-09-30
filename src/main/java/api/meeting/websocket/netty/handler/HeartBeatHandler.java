package api.meeting.websocket.netty.handler;

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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            switch (state) {
                case READER_IDLE -> {
                    Attribute<String> attribute = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().toString()));
                    String userId = attribute.get();
                    log.info("用户{}心跳超时", userId);
                    ctx.close();
                }
                case WRITER_IDLE -> ctx.writeAndFlush("hear");
            }
        }
    }
}
