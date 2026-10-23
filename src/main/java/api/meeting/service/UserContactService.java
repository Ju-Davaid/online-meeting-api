package api.meeting.service;

import api.meeting.entity.po.UserContact;
import api.meeting.entity.po.UserContactApply;
import api.meeting.entity.vo.UserSearchVO;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

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
     * @return 申请结果
     */
    int apply(String targetUserId);

    /**
     * 处理好友申请
     *
     * @param targetUserId 目标用户ID
     */
    void handleApply(String targetUserId, Integer status);

    /**
     * 获取用户好友列表
     *
     * @return 好友列表
     */
    List<UserContact> getUserContactList();

    /**
     * 获取用户好友申请列表
     *
     * @return 好友申请列表
     */
    List<UserContactApply> getUserContactApplyList();
}
