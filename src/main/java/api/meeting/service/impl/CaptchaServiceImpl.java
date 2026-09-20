package api.meeting.service.impl;

import api.meeting.constant.Constant;
import api.meeting.constant.RedisCacheKey;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.vo.CaptchaVo;
import api.meeting.exception.BusinessException;
import api.meeting.service.CaptchaService;
import api.meeting.utils.RedisUtils;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final RedisUtils<Integer> redisUtils;


    @Override
    public CaptchaVo getCaptcha(Integer width, Integer height) {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(width, height, 4, 4);
        captcha.setGenerator(new MathGenerator());
        captcha.createCode();
        int answer = (int) Calculator.conversion(captcha.getCode());
        String id = IdUtil.simpleUUID();
        redisUtils.set(RedisCacheKey.getCaptchaKey(id), answer, Constant.CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setId(id);
        captchaVo.setImage(captcha.getImageBase64Data());
        return captchaVo;
    }

    @Override
    public CaptchaVo refreshCaptcha(String id, Integer width, Integer height) {
        Integer value = redisUtils.get(RedisCacheKey.getCaptchaKey(id));
        if (value == null) {
            throw new BusinessException(ResponseCode.INVALID_CAPTCHA_ID);
        }
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(width, height, 4, 4);
        captcha.setGenerator(new MathGenerator());
        captcha.createCode();
        Integer answer = (int) Calculator.conversion(captcha.getCode());
        redisUtils.set(RedisCacheKey.getCaptchaKey(id), answer, Constant.CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);
        CaptchaVo captchaVo = new CaptchaVo();
        captchaVo.setId(id);
        captchaVo.setImage(captcha.getImageBase64Data());
        return captchaVo;
    }

    @Override
    public boolean verifyCaptcha(String captchaId, Integer captcha) {
        try {
            Integer value = redisUtils.get(RedisCacheKey.getCaptchaKey(captchaId));
            if (value == null) {
                return false;
            }
            return value.equals(captcha);
        } finally {
            redisUtils.delete(RedisCacheKey.getCaptchaKey(captchaId));
        }
    }
}
