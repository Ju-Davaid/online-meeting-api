package api.meeting.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录DTO
 */
@Data
public class UserLoginDTO {
    @Size(message = "用户名长度必须在 6-20 个字符之间", min = 6, max = 20)
    private String username;
    @Email(message = "邮箱格式错误")
    private String email;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotNull(message = "验证码不能为空")
    private Integer captcha;
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
}
