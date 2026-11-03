package api.meeting.service.impl;

import api.meeting.config.AppConfig;
import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.dto.SendChatMessageDTO;
import api.meeting.entity.dto.UploadFileDTO;
import api.meeting.entity.enums.*;
import api.meeting.entity.po.MeetingChatMessage;
import api.meeting.entity.po.User;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.MeetingChatMessageMapper;
import api.meeting.service.ChatMessageService;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.message.MessageHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息服务实现类
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<MeetingChatMessageMapper, MeetingChatMessage> implements ChatMessageService {
    private final AppConfig appConfig;
    private final MessageHandler messageHandler;
    private final MeetingChatMessageMapper meetingChatMessageMapper;


    @Override
    public List<MeetingChatMessage> getList(String maxMessageId, Integer pageSize) {
        User user = JwtTokenUtils.getUserInfo();
        int size = Math.min(pageSize == null ? 20 : pageSize, 100); // 防刷
        Long cursor = StringUtils.isNotBlank(maxMessageId) ? Long.parseLong(maxMessageId) : null;

        LambdaQueryWrapper<MeetingChatMessage> wrapper = new LambdaQueryWrapper<MeetingChatMessage>()
                .eq(MeetingChatMessage::getMeetingId, user.getMeetingId())
                .eq(MeetingChatMessage::getReceiveUserId, user.getId())
                .in(MeetingChatMessage::getReceiveType,
                        MessageSendType.GROUP.getCode(), MessageSendType.USER.getCode())
                .lt(cursor != null, MeetingChatMessage::getId, cursor)
                .orderByDesc(MeetingChatMessage::getId)
                .last("LIMIT " + (size + 1));
        List<MeetingChatMessage> rows = meetingChatMessageMapper.selectList(wrapper);

        boolean hasMore = rows.size() > size;
        if (hasMore) {
            rows = rows.subList(0, size);
        }
        Collections.reverse(rows);
        return rows;
    }

    @Override
    public void sendMessage(MeetingChatMessage message) {
        User user = JwtTokenUtils.getUserInfo();
        if (!ArrayUtil.contains(new Integer[]{MessageType.CHAT_TEXT_MESSAGE.getCode(), MessageType.CHAT_MEDIA_MESSAGE.getCode()}, message.getMessageType())) {
            throw new BusinessException("违法的消息类型");
        }
        MessageSendType receiveType = MessageSendType.getByCode(message.getReceiveType());
        if (receiveType == null) {
            throw new BusinessException("违法的接收类型");
        }
        if (receiveType == MessageSendType.USER && StrUtil.isEmpty(message.getReceiveUserId())) {
            throw new BusinessException("接收用户不能为空");
        }
        MessageType messageType = MessageType.getByCode(message.getMessageType());
        if (messageType == MessageType.CHAT_TEXT_MESSAGE) {
            if (StrUtil.isEmpty(message.getMessageContent())) {
                throw new BusinessException("消息内容不能为空");
            }
            message.setStatus(MessageStatus.SUCCESS.getCode());
        } else if (messageType == MessageType.CHAT_MEDIA_MESSAGE) {
            if (StrUtil.isEmpty(message.getFileName()) || message.getFileSize() == null || message.getFileType() == null) {
                throw new BusinessException("消息媒体信息不完整");
            }
            message.setStatus(MessageStatus.SENDING.getCode());
        }
        message.setSendTime(new Date());
        meetingChatMessageMapper.insert(message);
        MessageSendDTO<?> sendDTO = BeanUtil.copyProperties(message, MessageSendDTO.class);
        // 发送消息给接收用户
        if (receiveType == MessageSendType.USER) {
            sendDTO.setMessageSendType(MessageSendType.USER.getCode());
            messageHandler.sendMessage(sendDTO);
            sendDTO.setReceiveUserId(user.getId());
            messageHandler.sendMessage(sendDTO);
        } else {
            sendDTO.setMessageSendType(MessageSendType.GROUP.getCode());
            messageHandler.sendMessage(sendDTO);
        }
    }

    @Override
    public void uploadFile(UploadFileDTO dto) {
        try {
            User user = JwtTokenUtils.getUserInfo();
            String month = DateUtil.format(dto.getSendTime(), DatePattern.createFormatter("yyyy/MM"));
            String folderPath = appConfig.getStaticLocation() + month;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean success = folder.mkdirs();
                if (!success) {
                    throw new BusinessException("上传文件失败");
                }
            }

            String filePath = folderPath + "/" + dto.getMessageId();
            String fileName = dto.getFile().getOriginalFilename();
            assert fileName != null;
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            FileType fileType = FileType.getBySuffix(fileSuffix);
            if (fileType == FileType.IMAGE) {
                File tempFile = new File(appConfig.getTempLocation() + "/" + IdUtil.simpleUUID() + "." + fileSuffix);
                dto.getFile().transferTo(tempFile);
            }
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new BusinessException("上传文件失败");
        }
    }
}
