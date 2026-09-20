package api.meeting.service.impl;

import api.meeting.config.JwtConfig;
import api.meeting.entity.dto.UserLoginDTO;
import api.meeting.entity.dto.UserRegisterDTO;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.enums.UserGender;
import api.meeting.entity.enums.UserStatus;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.UserLoginVO;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.UserMapper;
import api.meeting.service.CaptchaService;
import api.meeting.service.UserService;
import api.meeting.utils.JwtTokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final CaptchaService captchaService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final JwtConfig jwtConfig;

    @Override
    public void register(UserRegisterDTO dto) {
        if (!captchaService.verifyCaptcha(dto.getCaptchaId(), dto.getCaptcha())) {
            throw new BusinessException(ResponseCode.INVALID_CAPTCHA);
        }
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
        if (count > 0) {
            throw new BusinessException("邮箱已存在");
        }
        count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
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

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        if (!captchaService.verifyCaptcha(dto.getCaptchaId(), dto.getCaptcha())) {
            throw new BusinessException(ResponseCode.INVALID_CAPTCHA);
        }
        if (!StringUtils.hasText(dto.getUsername()) && !StringUtils.hasText(dto.getEmail())) {
            throw new BusinessException("用户名或邮箱不能为空");
        }
        User user = userMapper.
                selectOne(new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, dto.getUsername())
                        .or()
                        .eq(User::getEmail, dto.getEmail()));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (!Objects.equals(user.getStatus(), UserStatus.NORMAL.getCode())) {
            throw new BusinessException(ResponseCode.USER_STATUS_ERROR);
        }
        UserLoginVO vo = new UserLoginVO();
        UserLoginVO.UserInfo userInfo = new UserLoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole("user");
        userInfo.setStatus(user.getStatus());
        userInfo.setGender(user.getGender());
        vo.setUserInfo(userInfo);
        vo.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()));
        JwtTokenUtils.Payload payload = new JwtTokenUtils.Payload();
        payload.setUserId(user.getId());
        payload.setUsername(user.getUsername());
        payload.setStatus(user.getStatus());
        String accessToken = jwtTokenUtils.generateToken(payload);
        vo.setAccessToken(accessToken);
        return vo;
    }
}
