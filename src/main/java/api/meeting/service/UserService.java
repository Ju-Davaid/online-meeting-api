package api.meeting.service;

import api.meeting.entity.dto.RegisterDTO;
import api.meeting.entity.po.User;
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
    void register(RegisterDTO dto);
}
