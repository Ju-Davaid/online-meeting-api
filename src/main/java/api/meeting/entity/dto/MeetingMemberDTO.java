package api.meeting.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 会议成员DTO
 */
@Data
public class MeetingMemberDTO {
    private String userId;
    private String nickName;
    private Date joinTime;
    private Integer gender;
    private Integer memberType;
    private Integer status;
}
