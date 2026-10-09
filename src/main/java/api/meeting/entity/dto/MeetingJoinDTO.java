package api.meeting.entity.dto;

import api.meeting.entity.po.MeetingMember;
import lombok.Data;

import java.util.List;

/**
 * 会议加入DTO
 */
@Data
public class MeetingJoinDTO {
    private MeetingMember newMember;
    private List<MeetingMember> memberList;
}
