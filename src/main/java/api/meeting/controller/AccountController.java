package api.meeting.controller;

import api.meeting.entity.vo.CaptchaVo;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.service.CaptchaService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 账户控制器
 */
@Slf4j
@RestController
@RequestMapping("/")
@Validated
@RequiredArgsConstructor
public class AccountController {
    private final CaptchaService captchaService;

    /**
     * 生成验证码
     *
     * @param width  验证码宽度
     * @param height 验证码高度
     * @return 验证码VO
     */
    @GetMapping("/captcha")
    public ResponseVO<CaptchaVo> captcha(@RequestParam(defaultValue = "200") Integer width, @RequestParam(defaultValue = "100") Integer height) {
        CaptchaVo captchaVo = captchaService.getCaptcha(width, height);
        return ResponseVO.success("验证码已生成", captchaVo);
    }

    /**
     * 刷新验证码
     *
     * @param id     验证码id
     * @param width  验证码宽度
     * @param height 验证码高度
     * @return 验证码VO
     */
    @GetMapping("/captcha/{id}")
    public ResponseVO<CaptchaVo> refreshCaptcha(@NotBlank(message = "验证码id不能为空") @PathVariable("id") String id, @RequestParam(defaultValue = "200") Integer width, @RequestParam(defaultValue = "100") Integer height) {
        CaptchaVo captchaVo = captchaService.refreshCaptcha(id, width, height);
        return ResponseVO.success("验证码已刷新", captchaVo);
    }
}
