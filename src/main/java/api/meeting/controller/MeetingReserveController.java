package api.meeting.controller;

import api.meeting.entity.po.MeetingMember;
import api.meeting.entity.po.MeetingReserve;
import api.meeting.entity.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/meeting/reserve")
public class MeetingReserveController {
    @GetMapping("/list")
    public ResponseVO<List<MeetingReserve>> listMeetingReserve() {
        return null;
    }
}
