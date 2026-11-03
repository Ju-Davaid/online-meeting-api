package api.meeting.controller;

import api.meeting.entity.dto.SendChatMessageDTO;
import api.meeting.entity.dto.UploadFileDTO;
import api.meeting.entity.enums.MessageSendType;
import api.meeting.entity.po.MeetingChatMessage;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.service.ChatMessageService;
import api.meeting.utils.JwtTokenUtils;
import cn.hutool.core.bean.BeanUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatMessageService chatMessageService;

    /**
     * 获取聊天消息列表
     *
     * @param maxMessageId 最大消息ID
     * @param pageSize     分页大小
     * @return 聊天消息列表
     */
    @GetMapping("/list")
    public ResponseVO<List<MeetingChatMessage>> list(@NotNull(message = "最大消息ID不能为空") String maxMessageId, @NotNull(message = "分页大小不能为空") Integer pageSize) {
        return ResponseVO.success("获取成功", chatMessageService.getList(maxMessageId, pageSize));
    }

    /**
     * 发送聊天消息
     *
     * @param dto 发送聊天消息DTO
     * @return 发送结果
     */
    @PostMapping("/send")
    public ResponseVO<?> send(@Validated @RequestBody SendChatMessageDTO dto) {
        User user = JwtTokenUtils.getUserInfo();
        MeetingChatMessage message = BeanUtil.copyProperties(dto, MeetingChatMessage.class);
        message.setMeetingId(user.getMeetingId());
        message.setSendUserId(user.getId());
        message.setSendUserNickname(user.getUsername());
        if (message.getReceiveUserId() == null) {
            message.setReceiveType(MessageSendType.GROUP.getCode());
        } else {
            message.setReceiveType(MessageSendType.USER.getCode());
        }
        chatMessageService.sendMessage(message);
        return ResponseVO.success("发送成功");
    }

    /**
     * 上传文件
     *
     * @param dto 上传文件DTO
     * @return 上传结果
     */
    @PostMapping("/uploadFile")
    public ResponseVO<?> uploadFile(@RequestBody UploadFileDTO dto) {
        return ResponseVO.success("上传成功");
    }
}
