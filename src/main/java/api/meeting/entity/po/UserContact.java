package api.meeting.entity.po;

import api.meeting.entity.vo.UserSearchVO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 用户联系人实体类
 */
@Data
public class UserContact {
    private String contactId;
    private String userId;
    private Integer status;
    private Date lastUpdateTime;
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private Integer userStatus;
    @TableField(exist = false)
    private Date lastLoginTime;
    @TableField(exist = false)
    private Date lastOffTime;
}
