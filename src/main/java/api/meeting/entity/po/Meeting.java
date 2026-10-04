package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会议实体类
 */
@Data
public class Meeting {
    @TableId
    String id;
    String number;
    Date createTime;
    String createdUserId;
    Integer joinType;
    String joinPassword;
    Date startTime;
    Date endTime;
    Integer status;
}
