package api.meeting.service;

import api.meeting.entity.dto.UserLoginDTO;
import api.meeting.entity.dto.UserRegisterDTO;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.UserLoginVO;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 注册用户
     *
     * @param dto 注册DTO
     */
    void register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录DTO
     */
    UserLoginVO login(UserLoginDTO dto);
}
