package api.meeting.service.impl;

import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.enums.MessageSendType;
import api.meeting.entity.enums.MessageType;
import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.enums.UserContactApplyStatus;
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
    public int apply(String targetUserId) {
        User currentUser = JwtTokenUtils.getUserInfo();
        // 检查目标用户是否拉黑了当前用户（联系人表）
        UserContact targetContact = userContactMapper.selectOne(new LambdaQueryWrapper<UserContact>()
                .eq(UserContact::getUserId, targetUserId)
                .eq(UserContact::getContactId, currentUser.getId())
        );
        if (targetContact != null && UserContactApplyStatus.BLACKED.getCode().equals(targetContact.getStatus())) {
            throw new BusinessException("对方已将你拉黑");
        }
        // 检查对方是否已添加你为好友
        if (targetContact != null && UserContactApplyStatus.FRIENDED.getCode().equals(targetContact.getStatus())) {
            UserContact updateContact = new UserContact();
            updateContact.setStatus(UserContactApplyStatus.FRIENDED.getCode());
            updateContact.setLastUpdateTime(new Date());
            // 更新当前用户联系人表
            userContactMapper.update(updateContact, new LambdaQueryWrapper<UserContact>()
                    .eq(UserContact::getUserId, currentUser.getId())
                    .eq(UserContact::getContactId, targetUserId));
            return UserContactApplyStatus.FRIENDED.getCode();
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
            currentUserContactApply.setStatus(UserContactApplyStatus.APPLYING.getCode());
            currentUserContactApply.setLastApplyTime(new Date());
        } else {
            currentUserContactApply.setStatus(UserContactApplyStatus.APPLYING.getCode());
            currentUserContactApply.setLastApplyTime(new Date());
            userContactApplyMapper.updateById(currentUserContactApply);
        }

        MessageSendDTO<Integer> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageSendType(MessageSendType.USER.getCode());
        sendDTO.setMessageType(MessageType.FRIEND_APPLY.getCode());
        sendDTO.setReceiveUserId(targetUserId);
        sendDTO.setMessageContent(UserContactApplyStatus.APPLYING.getCode());
        messageHandler.sendMessage(sendDTO);
        return UserContactApplyStatus.APPLYING.getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleApply(String targetUserId, Integer status) {
        UserContactApplyStatus applyStatus = UserContactApplyStatus.getByCode(status);
        if (applyStatus == null || UserContactApplyStatus.APPLYING.equals(applyStatus)) {
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
        if (UserContactApplyStatus.PASS.getCode().equals(applyStatus.getCode())) {
            Date now = new Date();
            UserContact newContact = new UserContact();
            newContact.setUserId(currentUser.getId());
            newContact.setContactId(targetUserId);
            newContact.setLastUpdateTime(now);
            newContact.setStatus(UserContactApplyStatus.FRIENDED.getCode());
            this.userContactInsertOrUpdate(newContact);
            // 互加好友
            newContact.setUserId(targetUserId);
            newContact.setContactId(currentUser.getId());
            this.userContactInsertOrUpdate(newContact);
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


    /**
     * 插入或更新用户联系人
     *
     * @param userContact 用户联系人
     */
    private void userContactInsertOrUpdate(UserContact userContact) {
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