package api.meeting.service.impl;

import api.meeting.constant.RedisCacheKey;
import api.meeting.entity.dto.*;
import api.meeting.entity.enums.*;
import api.meeting.entity.po.Meeting;
import api.meeting.entity.po.MeetingMember;
import api.meeting.entity.po.MeetingReserve;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.PageVO;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.MeetingMapper;
import api.meeting.mapper.MeetingMemberMapper;
import api.meeting.mapper.MeetingReserveMapper;
import api.meeting.service.MeetingService;
import api.meeting.service.UserService;
import api.meeting.utils.JwtTokenUtils;
import api.meeting.utils.RedisUtils;
import api.meeting.websocket.message.MessageHandler;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 会议服务实现类
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {
    private final MeetingMapper meetingMapper;
    private final UserService userService;
    private final MeetingMemberMapper meetingMemberMapper;
    private final ChannelContextUtil channelContextUtil;
    private final MessageHandler messageHandler;
    private final RedisUtils<MeetingMemberDTO> redisUtils;
    private final MeetingReserveMapper meetingReserveMapper;

    @Override
    public PageVO<Meeting> getMeetingList(Integer pageNum, Integer pageSize) {
        Page<Meeting> page = new Page<>(pageNum, pageSize);
        User user = JwtTokenUtils.getUserInfo();
        IPage<Meeting> meetings = meetingMapper.selectPage(page, new LambdaQueryWrapper<Meeting>().eq(Meeting::getCreatedUserId, user.getId()).eq(Meeting::getStatus, 1).orderByDesc(Meeting::getCreatedTime));
        PageVO<Meeting> pageVO = new PageVO<>();
        pageVO.setPageNum(pageNum);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(meetings.getTotal());
        pageVO.setList(meetings.getRecords());
        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void quickMeeting(QuickMeetingDTO quickMeetingDTO) {
        User user = JwtTokenUtils.getUserInfo();
        if (user.getMeetingId() != null) {
            throw new BusinessException("用户已加入会议");
        }
        Meeting meeting = new Meeting();
        meeting.setMeetingNo(RandomUtil.randomNumbers(5));
        meeting.setMeetingName(quickMeetingDTO.getMeetingName());
        meeting.setJoinType(quickMeetingDTO.getJoinType());
        meeting.setJoinPassword(quickMeetingDTO.getJoinPassword());
        meeting.setCreatedUserId(user.getId());
        meeting.setCreatedTime(new Date());
        meeting.setStatus(MeetingStatus.RUNNING.getCode());
        meetingMapper.insert(meeting);
        user.setMeetingId(meeting.getId());
        userService.updateById(user);
    }

    @Override
    public void joinMeeting(boolean videoOpen) {
        User user = JwtTokenUtils.getUserInfo();
        if (user != null && user.getMeetingId() == null) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        Meeting meeting = meetingMapper.selectById(user.getMeetingId());
        if (meeting == null || MeetingStatus.ENDED.getCode().equals(meeting.getStatus())) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        // 校验用户
        this.checkMeetingJoin(meeting.getId(), user.getId());
        // 加入成员
        MeetingMemberType memberType = meeting.getCreatedUserId().equals(user.getId()) ? MeetingMemberType.HOST : MeetingMemberType.NORMAL;
        this.addMeetingMember(meeting.getId(), user.getId(), user.getUsername(), memberType.getCode());
        // 加入会议
        this.addToMeeting(meeting.getId(), user.getId(), user.getUsername(), user.getGender(), memberType.getCode(), videoOpen);
        // 加入ws房间
        channelContextUtil.addMeetingRoom(meeting.getId(), user.getId());
        // 发送加入会议消息
        MeetingJoinDTO joinDTO = new MeetingJoinDTO();
        MeetingMemberDTO newMember = redisUtils.hGet(RedisCacheKey.getMeetingRoomKey(meeting.getId()), user.getId());
        joinDTO.setNewMember(newMember);
        List<MeetingMemberDTO> memberList = redisUtils.hValues(RedisCacheKey.getMeetingRoomKey(meeting.getId()));
        joinDTO.setMemberList(memberList);
        MessageSendDTO<MeetingJoinDTO> messageSendDTO = new MessageSendDTO<>();
        messageSendDTO.setMessageSendType(MessageSendType.GROUP.getCode());
        messageSendDTO.setMessageType(MessageType.JOIN.getCode());
        messageSendDTO.setMeetingId(meeting.getId());
        messageSendDTO.setMessageContent(joinDTO);
        messageHandler.sendMessage(messageSendDTO);
    }

    @Override
    public String preJoinMeeting(PreJoinMeetingDTO preJoinMeetingDTO) {
        User user = JwtTokenUtils.getUserInfo();
        List<Meeting> meetingList = meetingMapper.selectList(new LambdaQueryWrapper<Meeting>().eq(Meeting::getMeetingNo, preJoinMeetingDTO.getMeetingNo()).eq(Meeting::getStatus, MeetingStatus.RUNNING.getCode()).orderByDesc(Meeting::getCreatedTime));
        if (meetingList.isEmpty()) {
            throw new BusinessException("会议不存在");
        }
        Meeting meeting = meetingList.get(0);
        if (!meeting.getStatus().equals(MeetingStatus.RUNNING.getCode())) {
            throw new BusinessException("会议已结束");
        }
        if (!meeting.getId().equals(user.getMeetingId())) {
            throw new BusinessException("您有未结束的会议，不能加入加入其他会议");
        }
        this.checkMeetingJoin(meeting.getId(), user.getId());
        if (meeting.getJoinType().equals(MeetingJoinType.PASSWORD.getCode()) && !Objects.equals(meeting.getJoinPassword(), preJoinMeetingDTO.getJoinPassword())) {
            throw new BusinessException("密码错误");
        }
        user.setMeetingId(meeting.getId());
        userService.updateById(user);
        return meeting.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitMeeting(MeetingMemberStatus status) {
        User user = JwtTokenUtils.getUserInfo();
        String meetingId = user.getMeetingId();
        if (meetingId == null) return;
        String userId = user.getId();
        MeetingMemberDTO memberDTO = redisUtils.hGet(RedisCacheKey.getMeetingRoomKey(meetingId), userId);
        if (memberDTO != null) {
            memberDTO.setStatus(status.getCode());
            redisUtils.hSet(RedisCacheKey.getMeetingRoomKey(meetingId), userId, memberDTO);
        } else {
            user.setMeetingId(null);
            userService.updateById(user);
            return;
        }
        MessageSendDTO<MeetingExitDTO> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageType(MessageType.LEAVE.getCode());
        user.setMeetingId(null);
        userService.updateById(user);
        List<MeetingMemberDTO> memberList = redisUtils.hValues(RedisCacheKey.getMeetingRoomKey(meetingId));
        // 设置退出会议dto
        MeetingExitDTO exitDTO = new MeetingExitDTO();
        exitDTO.setMemberList(memberList);
        exitDTO.setExitUserId(userId);
        exitDTO.setExitMeetingStatus(status.getCode());
        // 设置消息内容
        sendDTO.setMessageContent(exitDTO);
        sendDTO.setMeetingId(meetingId);
        sendDTO.setSendUserId(userId);
        sendDTO.setSendUserNickname(user.getUsername());
        sendDTO.setMessageSendType(MessageSendType.GROUP.getCode());
        sendDTO.setSendTime(new Date());
        messageHandler.sendMessage(sendDTO);
        // 在线用户列表
        List<MeetingMemberDTO> onlineMemberDTOList = memberList.stream().filter(member -> member.getStatus().equals(MeetingMemberStatus.NORMAL.getCode())).toList();

        if (onlineMemberDTOList.isEmpty()) {
            endMeeting();
            return;
        }
        // 如果退出用户是拉黑或踢出状态，更新
        if (ArrayUtil.contains(new Integer[]{MeetingMemberStatus.BLACKLISTED.getCode(), MeetingMemberStatus.KICKED.getCode()}, status.getCode())) {
            MeetingMember meetingMember = new MeetingMember();
            meetingMember.setStatus(status.getCode());
            meetingMemberMapper.update(meetingMember, new LambdaQueryWrapper<MeetingMember>()
                    .eq(MeetingMember::getMeetingId, meetingId)
                    .eq(MeetingMember::getUserId, userId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceExitMeeting(String targetUserId, MeetingMemberStatus status) {
        User user = JwtTokenUtils.getUserInfo();
        if (targetUserId.equals(user.getId())) return;
        Meeting meeting = meetingMapper.selectById(user.getMeetingId());
        if (meeting != null && !meeting.getCreatedUserId().equals(user.getId())) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }
        exitMeeting(status);
    }

    @Override
    public Meeting getCurrentMeeting() {
        User user = JwtTokenUtils.getUserInfo();
        Meeting meeting = meetingMapper.selectById(user.getMeetingId());
        if (meeting != null && meeting.getStatus().equals(MeetingStatus.RUNNING.getCode())) {
            return meeting;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endMeeting() {
        User user = JwtTokenUtils.getUserInfo();
        Meeting meeting = meetingMapper.selectById(user.getMeetingId());
        if (meeting != null && !meeting.getCreatedUserId().equals(user.getId())) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }
        Meeting updateMeeting = new Meeting();
        updateMeeting.setStatus(MeetingStatus.ENDED.getCode());
        updateMeeting.setEndTime(new Date());
        meetingMapper.updateById(updateMeeting);
        // 发送结束会议消息
        MessageSendDTO<?> sendDTO = new MessageSendDTO<>();
        sendDTO.setMessageType(MessageType.END.getCode());
        sendDTO.setMessageSendType(MessageSendType.GROUP.getCode());
        sendDTO.setMessageId(meeting.getId());
        messageHandler.sendMessage(sendDTO);
        // 更新会议会员状态
        MeetingMember updateMeetingMember = new MeetingMember();
        updateMeetingMember.setMeetingStatus(MeetingStatus.ENDED.getCode());
        meetingMemberMapper.update(updateMeetingMember, new LambdaQueryWrapper<MeetingMember>()
                .eq(MeetingMember::getMeetingId, meeting.getId()));
        // TODO 更新预约用户状态

        List<MeetingMemberDTO> meetingMemberDTOList = redisUtils.hValues(RedisCacheKey.getMeetingRoomKey(meeting.getId()));
        // 更新用户会议ID为null
        List<String> userIdList = meetingMemberDTOList.stream().map(MeetingMemberDTO::getUserId).toList();
        List<User> updateUserList = userIdList.stream().map(userId -> {
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setMeetingId(null);
            return user;
        }).toList();
        userService.updateBatchById(updateUserList);
        // 删除会议房间中的用户
        redisUtils.hDel(RedisCacheKey.getMeetingRoomKey(meeting.getId()), userIdList.toArray());
    }

    @Override
    public void deleteMeeting() {
        User user = JwtTokenUtils.getUserInfo();
        MeetingMember updateMeetingMember = new MeetingMember();
        updateMeetingMember.setStatus(MeetingMemberStatus.DELETE.getCode());
        meetingMemberMapper.update(updateMeetingMember, new LambdaQueryWrapper<MeetingMember>()
                .eq(MeetingMember::getMeetingId, user.getMeetingId())
                .eq(MeetingMember::getUserId, user.getId())
        );
    }

    @Override
    public List<MeetingMember> getMeetingMembers() {
        User user = JwtTokenUtils.getUserInfo();
        List<MeetingMember> memberList = meetingMemberMapper.selectList(new LambdaQueryWrapper<MeetingMember>()
                .eq(MeetingMember::getMeetingId, user.getMeetingId())
        );
        Optional<MeetingMember> meetingMemberOptional = memberList.stream().filter(m -> m.getUserId().equals(user.getId())).findFirst();
        if (meetingMemberOptional.isEmpty()) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }
        return memberList;
    }

    @Override
    public List<MeetingReserve> getMeetingReserves() {
        User user = JwtTokenUtils.getUserInfo();
        return meetingReserveMapper.getMeetingReserve(user.getId(), MeetingReserveStatus.UN_START.getCode());
    }

    /**
     * 加入会议会员
     *
     * @param meetingId  会议ID
     * @param userId     用户ID
     * @param nickName   昵称
     * @param memberType 会员类型
     */
    private void addMeetingMember(String meetingId, String userId, String nickName, Integer memberType) {
        MeetingMember meetingMember = meetingMemberMapper.selectOne(new LambdaQueryWrapper<MeetingMember>().eq(MeetingMember::getMeetingId, meetingId).eq(MeetingMember::getUserId, userId));
        if (meetingMember == null) {
            meetingMember = new MeetingMember();
            meetingMember.setMeetingId(meetingId);
            meetingMember.setUserId(userId);
            meetingMember.setNickname(nickName);
            meetingMember.setMemberType(memberType);
            meetingMember.setLastJoinTime(new Date());
            meetingMember.setStatus(MeetingMemberStatus.NORMAL.getCode());
            meetingMember.setMeetingStatus(MeetingStatus.RUNNING.getCode());
            meetingMemberMapper.insert(meetingMember);
        } else {
            meetingMember.setLastJoinTime(new Date());
            meetingMemberMapper.updateById(meetingMember);
        }
    }

    /**
     * 加入会议
     *
     * @param meetingId  会议ID
     * @param userId     用户ID
     * @param nickName   昵称
     * @param gender     性别
     * @param memberType 会员类型
     * @param videoOpen  是否开启视频
     */
    private void addToMeeting(String meetingId, String userId, String nickName, Integer gender, Integer memberType, Boolean videoOpen) {
        MeetingMemberDTO meetingMemberDTO = new MeetingMemberDTO();
        meetingMemberDTO.setUserId(userId);
        meetingMemberDTO.setNickName(nickName);
        meetingMemberDTO.setJoinTime(new Date());
        meetingMemberDTO.setGender(gender);
        meetingMemberDTO.setStatus(MeetingMemberStatus.NORMAL.getCode());
        meetingMemberDTO.setMemberType(memberType);
        meetingMemberDTO.setMemberType(memberType);
        redisUtils.hSet(RedisCacheKey.getMeetingRoomKey(meetingId), userId, meetingMemberDTO);
    }

    /**
     * 检查用户是否加入会议
     *
     * @param meetingId 会议ID
     * @param userId    用户ID
     */
    private void checkMeetingJoin(String meetingId, String userId) {
        MeetingMemberDTO meetingMemberDTO = redisUtils.hGet(RedisCacheKey.getMeetingRoomKey(meetingId), userId);
        if (meetingMemberDTO != null && MeetingMemberStatus.BLACKLISTED.getCode().equals(meetingMemberDTO.getStatus())) {
            throw new BusinessException("用户已经被拉黑，不能加入会议");
        }
    }
}
