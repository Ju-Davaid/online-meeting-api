package api.meeting.service.impl;

import api.meeting.entity.enums.UserContactApplyStatus;
import api.meeting.entity.po.User;
import api.meeting.entity.po.UserContact;
import api.meeting.entity.po.UserContactApply;
import api.meeting.entity.vo.UserSearchVO;
import api.meeting.mapper.UserContactApplyMapper;
import api.meeting.mapper.UserContactMapper;
import api.meeting.mapper.UserMapper;
import api.meeting.service.UserContactService;
import api.meeting.utils.JwtTokenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户联系人服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactServiceImpl implements UserContactService {
    private final UserMapper userMapper;
    private final UserContactMapper userContactMapper;
    private final UserContactApplyMapper userContactApplyMapper;

    @Override
    public UserSearchVO searchUser(String userId) {
        // 当前用户
        User currentUser = JwtTokenUtils.getUserInfo();
        if (currentUser == null) {
            return null;
        }
        // 申请目标用户
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            return null;
        }

        UserSearchVO searchVO = new UserSearchVO();
        searchVO.setUserId(targetUser.getId());
        searchVO.setNickName(targetUser.getUsername());
        log.info("targetId:{} currentId:{}", searchVO.getUserId(), currentUser.getId());
        // 搜索自己 —— 直接返回，无意义做后续查询
        if (currentUser.getId().equals(targetUser.getId())) {
            searchVO.setStatus(-UserContactApplyStatus.PASS.getCode());
            return searchVO;
        }

        // 检查目标用户是否拉黑了当前用户（联系人表）
        UserContact targetUserContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, targetUser.getId())
                .eq(UserContact::getContactId, currentUser.getId()));

        if (targetUserContact != null
                && targetUserContact.getStatus().equals(UserContactApplyStatus.BLACKED.getCode())) {
            searchVO.setStatus(UserContactApplyStatus.BLACKED.getCode());
            return searchVO;
        }

        // 检查最新申请记录
        UserContactApply contactApply = userContactApplyMapper.selectOne(new LambdaQueryWrapper<UserContactApply>()
                .eq(UserContactApply::getApplyUserId, currentUser.getId())
                .eq(UserContactApply::getReceiveUserId, targetUser.getId())
                .orderByDesc(UserContactApply::getLastApplyTime)
                .last("LIMIT 1")
        );

        if (contactApply != null
                && contactApply.getStatus().equals(UserContactApplyStatus.BLACKED.getCode())) {
            searchVO.setStatus(UserContactApplyStatus.BLACKED.getCode());
            return searchVO;
        }
        if (contactApply != null
                && contactApply.getStatus().equals(UserContactApplyStatus.APPLYING.getCode())) {
            searchVO.setStatus(UserContactApplyStatus.APPLYING.getCode());
            return searchVO;
        }

        // 检查是否已是双向好友
        UserContact myUserContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, currentUser.getId())
                .eq(UserContact::getContactId, targetUser.getId()));

        if (targetUserContact != null
                && targetUserContact.getStatus().equals(UserContactApplyStatus.FRIENDED.getCode())
                && myUserContact != null
                && myUserContact.getStatus().equals(UserContactApplyStatus.FRIENDED.getCode())) {
            searchVO.setStatus(UserContactApplyStatus.FRIENDED.getCode());
            return searchVO;
        }
        return null;
    }

    @Override
    public void apply(String targetUserId) {
        User currentUser = JwtTokenUtils.getUserInfo();
        UserContactApply apply = new UserContactApply();
        apply.setReceiveUserId(targetUserId);
        apply.setApplyUserId(currentUser.getId());
        apply.setStatus(UserContactApplyStatus.APPLYING.getCode());
        apply.setLastApplyTime(new Date());
        userContactApplyMapper.insert(apply);
    }
}