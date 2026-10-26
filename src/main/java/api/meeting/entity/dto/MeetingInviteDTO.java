package api.meeting.entity.dto;

import lombok.Data;

/**
 * 会议邀请DTO
 */
@Data
public class MeetingInviteDTO {
    private String meetingName;
    private String inviteUserName;
    private String meetingId;
}
