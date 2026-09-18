package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDate createdAt;
    private LocalDate lastLoginTime;
    private LocalDate lastOffTime;
    private LocalDate meetingId;
}
