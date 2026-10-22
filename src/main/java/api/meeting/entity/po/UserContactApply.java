package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 用户联系人申请实体类
 */
@Data
public class UserContactApply {
    @TableId
    private String id;
    private String applyUserId;
    private String receiveUserId;
    private Date lastApplyTime;
    private Integer status;
}
