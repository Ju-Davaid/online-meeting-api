package api.meeting.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 */
@Data
public class PreJoinMeetingDTO {
    @NotNull(message = "会议编号不能为空")
    private String meetingNo;
    @NotEmpty(message = "昵称不能为空")
    private String nickName;
    private String joinPassword;

}
