package api.meeting.websocket.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通道上下文工具类
 */
@Slf4j
public class ChannelContextUtil {
    public static <T> void setAttribute(ChannelHandlerContext ctx, String key, T value) {
        AttributeKey<T> attributeKey = AttributeKey.valueOf(key);
        ctx.channel().attr(attributeKey).set(value);
    }

    public static <T> T getAttribute(ChannelHandlerContext ctx, String key) {
        AttributeKey<T> attributeKey = AttributeKey.valueOf(key);
        return ctx.channel().attr(attributeKey).get();
    }
}
