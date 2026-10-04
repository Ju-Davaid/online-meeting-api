package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会议成员实体类
 */
@Data
public class MeetingMember {
    String meetingId;
    String userId;
    String nickname;
    Date lastJoinDate;
    Integer status;
    Integer memberType;
    Integer meetingStatus;
}
