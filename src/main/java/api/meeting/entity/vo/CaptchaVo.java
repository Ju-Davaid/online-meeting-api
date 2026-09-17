package api.meeting.entity.vo;

import lombok.Data;

/**
 * 验证码VO
 */
@Data
public class CaptchaVo {
    private String id;
    private String image;
}
