package api.meeting.controller;

import api.meeting.entity.dto.CreateMeetingReserveDTO;
import api.meeting.entity.po.MeetingMember;
import api.meeting.entity.po.MeetingReserve;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.service.MeetingReserveService;
import api.meeting.service.MeetingService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/reserve")
public class MeetingReserveController {
    private final MeetingReserveService meetingReserveService;

    /**
     * 获取会议预约列表
     */
    @GetMapping("/list")
    public ResponseVO<List<MeetingReserve>> listMeetingReserve() {
        return ResponseVO.success("查询成功", meetingReserveService.getMeetingReserves());
    }

    /**
     * 创建会议预约
     *
     * @param dto 会议预约DTO
     * @return 创建成功响应
     */
    @PostMapping("/create")
    public ResponseVO<?> createMeetingReserve(@RequestBody @Validated CreateMeetingReserveDTO dto) {
        meetingReserveService.createMeetingReserve(dto);
        return ResponseVO.success("创建成功");
    }

    /**
     * 删除会议预约
     *
     * @param meetingId 会议预约ID
     * @param userId    用户ID
     * @return 删除成功响应
     */
    @GetMapping("/delete")
    public ResponseVO<?> deleteMeetingReserve(@NotEmpty(message = "会议ID不能为空") String meetingId, @NotEmpty(message = "用户ID不能为空") String userId) {
        meetingReserveService.deleteMeetingReserve(meetingId, userId);
        return ResponseVO.success("删除成功");
    }

    /**
     * 删除用户预约的会议
     *
     * @param meetingId 会议预约ID
     * @param userId    用户ID
     * @return 删除成功响应
     */
    @GetMapping("/deleteByUser")
    public ResponseVO<?> deleteMeetingReserveByUser(@NotEmpty(message = "会议ID不能为空") String meetingId, @NotEmpty(message = "用户ID不能为空") String userId) {
        meetingReserveService.deleteMeetingReserveByUser(meetingId, userId);
        return ResponseVO.success("删除成功");
    }

    @GetMapping("/today")
    public ResponseVO<List<MeetingReserve>> todayMeetingReserve() {
        return ResponseVO.success("查询成功", meetingReserveService.getTodayMeetingReserves());
    }
}
