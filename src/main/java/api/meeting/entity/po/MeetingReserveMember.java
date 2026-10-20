package api.meeting.entity.po;

import lombok.Data;

/**
 * 会议预约成员实体类
 */
@Data
public class MeetingReserveMember {
    private String meetingId;
    private String inviteUserId;
}
