package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 用户联系人实体类
 */
@Data
public class UserContact {
    @TableId
    private String contactId;
    private String userId;
    private Integer status;
    private Date lastUpdateTime;
}
