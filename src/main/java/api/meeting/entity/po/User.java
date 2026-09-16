package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户实体类
 */
@TableName("user")
@Data
public class User {
    @TableId
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Integer gender;
    private Integer status;
    private LocalDate createdAt;
    private LocalDate lastLoginTime;
    private LocalDate lastOffTime;
    private LocalDate meetingId;
}
