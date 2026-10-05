package api.meeting.service;

import api.meeting.entity.vo.CaptchaVO;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    /**
     * 获取验证码
     *
     * @param width  验证码宽度
     * @param height 验证码高度
     * @return 验证码VO
     */
    CaptchaVO getCaptcha(Integer width, Integer height);

    /**
     * 刷新验证码
     *
     * @param id     验证码id
     * @param width  验证码宽度
     * @param height 验证码高度
     * @return 验证码VO
     */
    CaptchaVO refreshCaptcha(String id, Integer width, Integer height);

    /**
     * 验证验证码
     *
     * @param captchaId 验证码ID
     * @param captcha   验证码
     * @return 是否验证成功
     */
    boolean verifyCaptcha(String captchaId, String captcha);
}
