package api.meeting.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户登录VO
 */
@Data
public class UserLoginVO {
    private String accessToken;
    private String refreshToken;
    private Date expiration;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private String id;
        private String username;
        private String email;
        private String role;
        private Integer status;
        private Integer gender;
    }
}
