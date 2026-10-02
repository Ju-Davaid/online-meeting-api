package api.meeting.websocket.netty.utils;

import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.enums.MessageType;
import api.meeting.entity.po.User;
import api.meeting.service.UserService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道上下文工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelContextUtil {
    public static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ChannelGroup> MEETING_ROOM_CONTEXT_MAP = new ConcurrentHashMap<>();
    private final UserService userService;

    /**
     * 设置channel属性
     *
     * @param ctx   通道上下文
     * @param key   属性键
     * @param value 属性值
     * @param <T>   属性类型
     */
    public <T> void setAttribute(ChannelHandlerContext ctx, String key, T value) {
        AttributeKey<T> attributeKey = AttributeKey.valueOf(key);
        ctx.channel().attr(attributeKey).set(value);
    }

    /**
     * 获取channel属性
     *
     * @param ctx 通道上下文
     * @param key 属性键
     * @param <T> 属性类型
     * @return 属性值
     */
    public <T> T getAttribute(ChannelHandlerContext ctx, String key) {
        AttributeKey<T> attributeKey = AttributeKey.valueOf(key);
        return ctx.channel().attr(attributeKey).get();
    }

    /**
     * 添加用户上下文
     *
     * @param userId  用户ID
     * @param channel 通道
     */
    public void addContext(String userId, Channel channel) {
        try {
            String channelId = channel.id().toString();
            AttributeKey<String> attributeKey = null;
            if (!AttributeKey.exists(channelId)) {
                attributeKey = AttributeKey.newInstance(channelId);
            } else {
                attributeKey = AttributeKey.valueOf(channelId);
            }
            channel.attr(attributeKey).set(userId);
            User user = new User();
            user.setId(userId);
            user.setLastLoginTime(new Date());
            userService.updateById(user);
            USER_CONTEXT_MAP.put(userId, channel);
        } catch (Exception e) {
            log.error("初始化连接失败", e);
        }
    }

    /**
     * 添加用户房间上下文
     *
     * @param meetingId 会议ID
     * @param userId    用户ID
     */
    public void addMeetingRoom(String meetingId, String userId) {
        // 用户channel不存在则直接返回
        Channel context = USER_CONTEXT_MAP.get(userId);
        if (context == null) {
            return;
        }
        // 会议房间channel组不存在则创建
        ChannelGroup channelGroup = MEETING_ROOM_CONTEXT_MAP.computeIfAbsent(meetingId, k -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        Channel channel = channelGroup.find(context.id());
        if (channel == null) {
            channelGroup.add(context);
        }
    }

    /**
     * 发送消息
     *
     * @param messageSendDTO 消息发送DTO
     */
    public void sendMessage(MessageSendDTO<?> messageSendDTO) {
        if (MessageType.isUser(messageSendDTO.getMessageType())) {
            sendMessageForUser(messageSendDTO);
        } else {
            sendMessageForGroup(messageSendDTO);
        }
    }

    private void sendMessageForGroup(MessageSendDTO<?> messageSendDTO) {
        if (messageSendDTO.getMeetingId() == null) {
            return;
        }
        ChannelGroup channelGroup = MEETING_ROOM_CONTEXT_MAP.get(messageSendDTO.getMeetingId());
        if (channelGroup == null) {
            return;
        }
        channelGroup.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(messageSendDTO)));
    }

    private void sendMessageForUser(MessageSendDTO<?> messageSendDTO) {
        if (messageSendDTO.getReceiveUserId() == null) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.get(messageSendDTO.getReceiveUserId());
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(messageSendDTO)));
        }
    }

    /**
     * 关闭用户上下文
     *
     * @param userId 用户ID
     */
    public void closeContext(String userId) {
        if (StrUtil.isEmptyIfStr(userId)) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.remove(userId);
        if (channel != null) {
            channel.close();
        }
    }
}
