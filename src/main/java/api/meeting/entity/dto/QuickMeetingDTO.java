package api.meeting.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 快速会议DTO
 */
@Data
public class QuickMeetingDTO {
    @NotNull(message = "会议编号类型不能为空")
    private Integer meetingNoType;
    @NotEmpty(message = "会议名称不能为空")
    @Size(max = 100, message = "会议名称最多100个字符")
    private String meetingName;
    @NotNull(message = "加入方式不能为空")
    private Integer joinType;
    @NotEmpty(message = "会议密码不能为空")
    @Size(max = 5, message = "会议密码最多5个字符")
    private String joinPassword;
}
