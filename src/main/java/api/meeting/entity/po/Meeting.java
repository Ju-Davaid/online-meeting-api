package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 会议实体类
 */
@Data
public class Meeting {
    @TableId
    String id;
    String meetingNo;
    String meetingName;
    Date createdTime;
    String createdUserId;
    Integer joinType;
    String joinPassword;
    Date startTime;
    Date endTime;
    Integer status;
    @TableField(exist = false)
    Integer memberCount;
}
