package api.meeting.controller;

import api.meeting.entity.dto.UserLoginDTO;
import api.meeting.entity.dto.UserRegisterDTO;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.CaptchaVo;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.entity.vo.UserLoginVO;
import api.meeting.exception.BusinessException;
import api.meeting.service.CaptchaService;
import api.meeting.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;

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
    @GetMapping("/captcha/refresh")
    public ResponseVO<CaptchaVo> refreshCaptcha(@NotBlank(message = "验证码id不能为空") @RequestParam("id") String id, @RequestParam(defaultValue = "200") Integer width, @RequestParam(defaultValue = "100") Integer height) {
        log.info("刷新验证码，id：{}，width：{}，height：{}", id, width, height);
        CaptchaVo captchaVo = captchaService.refreshCaptcha(id, width, height);
        return ResponseVO.success("验证码已刷新", captchaVo);
    }

    /**
     * 注册
     *
     * @param dto 注册DTO
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseVO<?> register(@Validated @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return ResponseVO.success("注册成功");
    }

    /**
     * 登录
     *
     * @param dto 登录DTO
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseVO<UserLoginVO> login(@Validated @RequestBody UserLoginDTO dto) {
        UserLoginVO vo = userService.login(dto);
        return ResponseVO.success("登录成功", vo);
    }

    @GetMapping("/userInfo")
    public ResponseVO<UserLoginVO.UserInfo> getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }
        UserLoginVO.UserInfo userInfo = BeanUtil.copyProperties(user, UserLoginVO.UserInfo.class);
        return ResponseVO.success("获取用户信息成功", userInfo);
    }

    /**
     * 退出登录
     *
     * @return 退出登录结果
     */
    @GetMapping("/logout")
    public ResponseVO<?> logout(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }
        userService.logout(user);
        return ResponseVO.success("退出登录成功");
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseVO<?> test() {
        return ResponseVO.success("测试成功");
    }
}
