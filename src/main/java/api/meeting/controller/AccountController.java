package api.meeting.controller;

import api.meeting.constant.Constant;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j(topic = "api.meeting.controller.AccountController")
@RestController
@RequestMapping("/")
@Validated
public class AccountController {
    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response, @RequestParam(defaultValue = "200") Integer width, @RequestParam(defaultValue = "100") Integer height) throws IOException {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(width, height,4,4);
        captcha.setGenerator(new MathGenerator());
        captcha.createCode();
        HttpSession session = request.getSession();
        int answer = (int) Calculator.conversion(captcha.getCode());
        session.setAttribute(Constant.CAPTCHA_SESSION_Key, answer);
        log.info("captcha: {}", answer);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        captcha.write(response.getOutputStream());
    }
}
