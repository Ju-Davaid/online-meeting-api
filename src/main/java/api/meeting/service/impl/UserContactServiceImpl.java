package api.meeting.service.impl;

import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.enums.MessageSendType;
import api.meeting.entity.enums.MessageType;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.enums.UserContactStatus;
import api.meeting.entity.po.User;
import api.meeting.entity.po.UserContact;
import api.meeting.entity.po.UserContactApply;
import api.meeting.entity.vo.UserSearchVO;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.UserContactApplyMapper;
import api.meeting.mapper.UserContactMapper;
import api.meeting.mapper.UserMapper;
import api.meeting.service.UserContactService;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.websocket.message.MessageHandler;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    private final MessageHandler messageHandler;

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
            searchVO.setStatus(-UserContactStatus.PASS.getCode());
            return searchVO;
        }

        // 检查目标用户是否拉黑了当前用户（联系人表）
        UserContact targetUserContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, targetUser.getId())
                .eq(UserContact::getContactId, currentUser.getId()));

        if (targetUserContact != null
                && targetUserContact.getStatus().equals(UserContactStatus.BLACKED.getCode())) {
            searchVO.setStatus(UserContactStatus.BLACKED.getCode());
            return searchVO;
        }

        // 检查最新申请记录
        UserContactApply contactApply = userContactApplyMapper.selectOne(new LambdaQueryWrapper<UserContactApply>()
                .eq(UserContactApply::getApplyUserId, currentUser.getId())
                .eq(UserContactApply::getReceiveUserId, targetUser.getId())
        );

        if (contactApply != null
                && contactApply.getStatus().equals(UserContactStatus.BLACKED.getCode())) {
            searchVO.setStatus(UserContactStatus.BLACKED.getCode());
            return searchVO;
        }
        if (contactApply != null
                && contactApply.getStatus().equals(UserContactStatus.APPLYING.getCode())) {
            searchVO.setStatus(UserContactStatus.APPLYING.getCode());
            return searchVO;
        }

        // 检查是否已是双向好友
        UserContact myUserContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, currentUser.getId())
                .eq(UserContact::getContactId, targetUser.getId()));

        if (targetUserContact != null
                && targetUserContact.getStatus().equals(UserContactStatus.FRIENDED.getCode())
                && myUserContact != null
                && myUserContact.getStatus().equals(UserContactStatus.FRIENDED.getCode())) {
            searchVO.setStatus(UserContactStatus.FRIENDED.getCode());
            return searchVO;
        }
        return null;
    }

    @Override
    public int apply(String targetUserId) {
        User currentUser = JwtTokenUtils.getUserInfo();
        // 检查目标用户是否拉黑了当前用户（联系人表）
        UserContact targetContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, targetUserId)
                .eq(UserContact::getContactId, currentUser.getId())
        );
        if (targetContact != null && UserContactStatus.BLACKED.getCode().equals(targetContact.getStatus())) {
            throw new BusinessException("对方已将你拉黑");
        }
        // 检查对方是否已添加你为好友
        if (targetContact != null && UserContactStatus.FRIENDED.getCode().equals(targetContact.getStatus())) {
            UserContact updateContact = new UserContact();
            updateContact.setStatus(UserContactStatus.FRIENDED.getCode());
            updateContact.setLastUpdateTime(new Date());
            // 更新当前用户联系人表
            userContactMapper.update(updateContact, new LambdaQueryWrapper<UserContact>()
                    .eq(UserContact::getUserId, currentUser.getId())
                    .eq(UserContact::getContactId, targetUserId));
            return UserContactStatus.FRIENDED.getCode();
        }
        // 检查当前用户是否已申请过目标用户
        UserContactApply currentUserContactApply = userContactApplyMapper.selectOne(
                new LambdaQueryWrapper<UserContactApply>()
                        .eq(UserContactApply::getApplyUserId, currentUser.getId())
                        .eq(UserContactApply::getReceiveUserId, targetUserId)
        );
        if (currentUserContactApply == null) {
            currentUserContactApply = new UserContactApply();
            currentUserContactApply.setApplyUserId(currentUser.getId());
            currentUserContactApply.setReceiveUserId(targetUserId);
            currentUserContactApply.setStatus(UserContactStatus.APPLYING.getCode());
            currentUserContactApply.setLastApplyTime(new Date());
        } else {
            currentUserContactApply.setStatus(UserContactStatus.APPLYING.getCode());
            currentUserContactApply.setLastApplyTime(new Date());
            userContactApplyMapper.updateById(currentUserContactApply);
        }

        MessageSendDTO<Integer> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageSendType(MessageSendType.USER.getCode());
        sendDTO.setMessageType(MessageType.FRIEND_APPLY.getCode());
        sendDTO.setReceiveUserId(targetUserId);
        sendDTO.setMessageContent(UserContactStatus.APPLYING.getCode());
        messageHandler.sendMessage(sendDTO);
        return UserContactStatus.APPLYING.getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleApply(String targetUserId, Integer status) {
        UserContactStatus applyStatus = UserContactStatus.getByCode(status);
        if (applyStatus == null || UserContactStatus.APPLYING.equals(applyStatus)) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        User currentUser = JwtTokenUtils.getUserInfo();
        // 检查当前用户是否已申请过目标用户
        UserContactApply currentUserApply = userContactApplyMapper.selectOne(
                new LambdaQueryWrapper<UserContactApply>()
                        .eq(UserContactApply::getApplyUserId, targetUserId)
                        .eq(UserContactApply::getReceiveUserId, currentUser.getId())
        );
        if (currentUserApply == null) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        if (UserContactStatus.PASS.getCode().equals(applyStatus.getCode())) {
            Date now = new Date();
            UserContact newContact = new UserContact();
            newContact.setUserId(currentUser.getId());
            newContact.setContactId(targetUserId);
            newContact.setLastUpdateTime(now);
            newContact.setStatus(UserContactStatus.FRIENDED.getCode());
            this.insertOrUpdateUserContact(newContact);
            // 互加好友
            newContact.setUserId(targetUserId);
            newContact.setContactId(currentUser.getId());
            this.insertOrUpdateUserContact(newContact);
        }
        // 更新好友申请状态
        currentUserApply.setStatus(applyStatus.getCode());
        userContactApplyMapper.updateById(currentUserApply);
        // 发送好友申请结果消息
        MessageSendDTO<Integer> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageSendType(MessageSendType.USER.getCode());
        sendDTO.setMessageType(MessageType.HANDLE_APPLY.getCode());
        sendDTO.setReceiveUserId(targetUserId);
        sendDTO.setMessageContent(applyStatus.getCode());
        messageHandler.sendMessage(sendDTO);
    }

    @Override
    public List<UserContact> getUserContactList() {
        User currentUser = JwtTokenUtils.getUserInfo();
        // 查询当前用户的所有好友
        return userContactMapper.getUserContactList(currentUser.getId());
    }

    @Override
    public List<UserContactApply> getUserContactApplyList() {
        User currentUser = JwtTokenUtils.getUserInfo();
        // 查询当前用户的所有好友申请
        return userContactApplyMapper.getUserContactApplyList(currentUser.getId());
    }

    @Override
    public long getApplyCount() {
        User user = JwtTokenUtils.getUserInfo();
        return userContactApplyMapper.selectCount(new LambdaQueryWrapper<UserContactApply>()
                .eq(UserContactApply::getReceiveUserId, user.getId())
                .eq(UserContactApply::getStatus, UserContactStatus.APPLYING.getCode())
        );
    }

    @Override
    public void deleteContact(String contactId, Integer status) {
        User currentUser = JwtTokenUtils.getUserInfo();
        if (!ArrayUtil.contains(new Integer[]{UserContactStatus.DELETE.getCode(), UserContactStatus.BLACKED.getCode()}, status)) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        UserContact userContact = new UserContact();
        userContact.setLastUpdateTime(new Date());
        userContact.setStatus(status);
        userContactMapper.update(userContact, new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, currentUser.getId())
                .eq(UserContact::getContactId, contactId));
    }


    /**
     * 插入或更新用户联系人
     *
     * @param userContact 用户联系人
     */
    private void insertOrUpdateUserContact(UserContact userContact) {
        try {
            UserContact contact = userContactMapper.selectOne(
                    new LambdaQueryWrapper<UserContact>()
                            .eq(UserContact::getUserId, userContact.getUserId())
                            .eq(UserContact::getContactId, userContact.getContactId())
            );
            if (contact == null) {
                userContactMapper.insert(userContact);
            } else {
                userContactMapper.update(userContact, new LambdaQueryWrapper<UserContact>()
                        .eq(UserContact::getUserId, userContact.getUserId())
                        .eq(UserContact::getContactId, userContact.getContactId()));
            }
        } catch (Exception e) {
            log.error("插入或更新用户联系人失败", e);
            throw new BusinessException("插入或更新用户联系人失败");
        }
    }
}