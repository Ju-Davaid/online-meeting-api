package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 会议预约实体类
 */
@Data
public class MeetingReserve {
    @TableId
    private String meetingId;
    private String meetingName;
    private Integer joinType;
    private String joinPassword;
    private Long duration;
    private Date startTime;
    private Date createdTime;
    private String createdUserId;
    private Integer status;
}
