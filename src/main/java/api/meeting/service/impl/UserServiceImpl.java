package api.meeting.service.impl;

import api.meeting.entity.dto.RegisterDTO;
import api.meeting.entity.enums.UserGender;
import api.meeting.entity.enums.UserStatus;
import api.meeting.entity.po.User;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.UserMapper;
import api.meeting.service.CaptchaService;
import api.meeting.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final CaptchaService captchaService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO dto) {
        if (!captchaService.verifyCaptcha(dto.getCaptchaId(), dto.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
        if (count > 0) {
            throw new BusinessException("邮箱已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setStatus(UserStatus.NORMAL.getCode());
        user.setGender(UserGender.UNKNOWN.getCode());
        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("注册失败");
        }
    }
}
