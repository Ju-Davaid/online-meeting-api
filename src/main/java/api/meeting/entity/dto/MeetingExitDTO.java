package api.meeting.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 会议退出DTO
 */
@Data
public class MeetingExitDTO {
    private String exitUserId;
    private List<MeetingMemberDTO> memberList;
    private Integer exitMeetingStatus;
}
