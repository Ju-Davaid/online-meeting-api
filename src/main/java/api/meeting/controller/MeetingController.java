package api.meeting.controller;

import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.dto.PreJoinMeetingDTO;
import api.meeting.entity.dto.QuickMeetingDTO;
import api.meeting.entity.po.Meeting;
import api.meeting.entity.vo.PageVO;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.service.MeetingService;
import api.meeting.websocket.message.MessageHandler;
import api.meeting.websocket.message.MessageHandlerForRedis;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 会议控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;
    private final MessageHandler messageHandler;

    /**
     * 获取会议列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 会议列表
     */
    @GetMapping("/list")
    public ResponseVO<PageVO<Meeting>> getAllMeetings(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseVO.success("获取会议列表成功", meetingService.getMeetingList(pageNum, pageSize));
    }

    /**
     * 快速创建会议
     *
     * @param quickMeetingDTO 快速创建会议DTO
     * @return 会议ID
     */
    @PostMapping("/quick")
    public ResponseVO<?> quickMeeting(@Validated @RequestBody QuickMeetingDTO quickMeetingDTO) {
        meetingService.quickMeeting(quickMeetingDTO);
        return ResponseVO.success("快速创建会议成功");
    }

    /**
     * 预加入会议
     *
     * @param preJoinMeetingDTO 预加入会议DTO
     * @return 预加入会议成功
     */
    @PostMapping("/preJoin")
    public ResponseVO<?> preJoinMeeting(@Validated @RequestBody PreJoinMeetingDTO preJoinMeetingDTO) {
        preJoinMeetingDTO.setMeetingNo(preJoinMeetingDTO.getMeetingNo().replace(" ", ""));
        return ResponseVO.success("预加入会议成功", meetingService.preJoinMeeting(preJoinMeetingDTO));
    }

    /**
     * 加入会议 (进入会议)
     *
     * @param videoOpen 是否开启视频
     * @return 加入会议成功
     */
    @GetMapping("/join")
    public ResponseVO<?> joinMeeting(@NotNull(message = "是否开启视频不能为空") @RequestParam Boolean videoOpen) {
        meetingService.joinMeeting(videoOpen);
        return ResponseVO.success("加入会议成功");
    }

    @GetMapping("/testSendMessage")
    public ResponseVO<?> testSendMessage() {
        MessageSendDTO<String> messageSendDTO = new MessageSendDTO<>();
        messageSendDTO.setMessageContent("测试消息");
        messageHandler.sendMessage(messageSendDTO);
        return ResponseVO.success("测试发送消息成功");
    }
}
