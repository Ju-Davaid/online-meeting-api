package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会议成员实体类
 */
@Data
public class MeetingMember {
    @TableId
    String  id;
    String meetingId;
    String userId;
    String nickname;
    Date lastJoinTime;
    Integer status;
    Integer memberType;
    Integer meetingStatus;
}
