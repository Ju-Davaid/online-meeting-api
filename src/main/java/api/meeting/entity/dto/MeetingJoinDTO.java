package api.meeting.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 会议加入DTO
 */
@Data
public class MeetingJoinDTO {
    private MeetingMemberDTO newMember;
    private List<MeetingMemberDTO> memberList;
}
