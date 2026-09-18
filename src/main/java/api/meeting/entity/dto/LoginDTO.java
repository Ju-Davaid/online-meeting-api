package api.meeting.entity.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String email;
    private String password;
    private String captcha;
    private String captchaId;
}
