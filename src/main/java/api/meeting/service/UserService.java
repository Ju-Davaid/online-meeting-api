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

    /**
     * 退出登录
     *
     * @param user 用户实体
     */
    void logout(User user);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    User getById(String userId);

    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 是否更新成功
     */
    boolean updateById(User user);
}
