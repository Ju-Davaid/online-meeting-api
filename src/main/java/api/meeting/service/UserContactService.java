package api.meeting.service;

import api.meeting.entity.po.UserContact;
import api.meeting.entity.vo.UserSearchVO;
import com.baomidou.mybatisplus.spring.service.IService;

public interface UserContactService {
    /**
     * 搜索用户联系人
     *
     * @param userId 用户ID
     * @return 用户搜索VO
     */
    UserSearchVO searchUser(String userId);

    /**
     * 申请好友
     *
     * @param targetUserId 目标用户ID
     */

    void apply(String targetUserId);
}
