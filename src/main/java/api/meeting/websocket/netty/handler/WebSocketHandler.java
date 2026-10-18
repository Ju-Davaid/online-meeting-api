package api.meeting.websocket.netty.handler;

import api.meeting.constant.Constant;
import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.dto.PeerConnectionDataDTO;
import api.meeting.entity.dto.PeerMessageDTO;
import api.meeting.entity.enums.MessageSendType;
import api.meeting.entity.enums.MessageType;
import api.meeting.entity.po.User;
import api.meeting.service.UserService;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.message.MessageHandler;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.json.JSONUtil;
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
    private final MessageHandler messageHandler;
    private final UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String content = msg.text();
        if (Constant.PING.equalsIgnoreCase(content)) {
            return;
        }
        JwtTokenUtils.Payload payload = channelContextUtil.getAttribute(ctx, Constant.NETTY_TOKEN_PAYLOAD_KEY);
        log.info("收到{}消息: {}", payload.getUsername(), content);
        // 解析消息
        PeerConnectionDataDTO peerConnectionDataDTO = JSONUtil.toBean(content, PeerConnectionDataDTO.class);
        // 构建发送消息
        MessageSendDTO<PeerMessageDTO> sendDTO = buildSendDTO(peerConnectionDataDTO, payload);
        messageHandler.sendMessage(sendDTO);
    }

    /**
     * 构建发送消息
     *
     * @param peerConnectionDataDTO 消息连接数据DTO
     * @param payload               JWT 令牌负载
     * @return 消息发送DTO
     */
    private MessageSendDTO<PeerMessageDTO> buildSendDTO(PeerConnectionDataDTO peerConnectionDataDTO, JwtTokenUtils.Payload payload) {
        MessageSendDTO<PeerMessageDTO> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageSendType(MessageType.PEER.getCode());
        // 构建发送消息内容
        PeerMessageDTO peerMessageDTO = new PeerMessageDTO();
        peerMessageDTO.setSignalType(peerConnectionDataDTO.getSignalType());
        peerMessageDTO.setSignalData(peerConnectionDataDTO.getSignalData());
        // 补充发送消息信息
        sendDTO.setMessageContent(peerMessageDTO);
        User user = userService.getById(payload.getUserId());
        sendDTO.setMeetingId(user.getMeetingId());
        sendDTO.setSendUserId(payload.getUserId());
        sendDTO.setReceiveUserId(peerConnectionDataDTO.getReceiveUserId());
        sendDTO.setMessageSendType(MessageSendType.USER.getCode());
        return sendDTO;
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
        if (payload != null) {
            log.info("用户{}连接断开", payload.getUsername());
        }
    }
}
