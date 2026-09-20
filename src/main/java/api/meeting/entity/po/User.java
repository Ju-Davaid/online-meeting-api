package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
public class User {
    @TableId
    private String id;
    private String username;
    private String email;
    private String password;
    private Integer gender;
    private Integer status;
    private Date createdAt;
    private Date lastLoginTime;
    private Date lastOffTime;
    private String meetingId;
    private Integer role;
    @TableField(exist = false)
    private String token;
}
