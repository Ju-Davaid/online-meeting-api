package api.meeting.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * 会议预约DTO
 */
@Data
public class CreateMeetingReserveDTO {
    @NotEmpty(message = "会议名称不能为空")
    @Size(max = 20, message = "会议名称最多20个字符")
    private String meetingName;
    @NotNull(message = "加入方式不能为空")
    private Integer joinType;
    private String joinPassword;
    @NotNull(message = "会议时长不能为空")
    private Long duration;
    @NotNull(message = "会议开始时间不能为空")
    private Date startTime;
    private String inviteUserIds;
}
