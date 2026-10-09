package api.meeting.service.impl;

import api.meeting.constant.RedisCacheKey;
import api.meeting.entity.dto.MeetingJoinDTO;
import api.meeting.entity.dto.MeetingMemberDTO;
import api.meeting.entity.dto.MessageSendDTO;
import api.meeting.entity.dto.QuickMeetingDTO;
import api.meeting.entity.enums.*;
import api.meeting.entity.po.Meeting;
import api.meeting.entity.po.MeetingMember;
import api.meeting.entity.po.User;
import api.meeting.entity.vo.PageVO;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.MeetingMapper;
import api.meeting.mapper.MeetingMemberMapper;
import api.meeting.mapper.UserMapper;
import api.meeting.service.MeetingService;
import api.meeting.utils.RedisUtils;
import api.meeting.websocket.netty.utils.ChannelContextUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 会议服务实现类
 */
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {
    private final MeetingMapper meetingMapper;
    private final UserMapper userMapper;
    private final MeetingMemberMapper meetingMemberMapper;
    private final ChannelContextUtil channelContextUtil;
    private final RedisUtils<MeetingMemberDTO> redisUtils;

    @Override
    public PageVO<Meeting> getMeetingList(Integer pageNum, Integer pageSize) {
        Page<Meeting> page = new Page<>(pageNum, pageSize);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        IPage<Meeting> meetings = meetingMapper.selectPage(page,
                new LambdaQueryWrapper<Meeting>()
                        .eq(Meeting::getCreatedUserId, user.getId())
                        .eq(Meeting::getStatus, 1)
                        .orderByDesc(Meeting::getCreatedTime));
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
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinMeeting(boolean videoOpen) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null && user.getMeetingId() == null) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        Meeting meeting = meetingMapper.selectById(user.getMeetingId());
        if (meeting == null || MeetingStatus.ENDED.getCode().equals(meeting.getStatus())) {
            throw new BusinessException(ResponseCode.ILLEGAL_PARAM);
        }
        // 校验用户
        checkMemberJoin(meeting.getId(), user.getId());
        // 加入成员
        MeetingMemberType memberType = meeting.getCreatedUserId().equals(user.getId()) ? MeetingMemberType.HOST : MeetingMemberType.NORMAL;
        addMeetingMember(meeting.getId(), user.getId(), user.getUsername(), memberType.getCode());
        // 加入会议
        this.addToMeeting(meeting.getId(), user.getId(), user.getUsername(), user.getGender(), memberType.getCode(), videoOpen);
        // 加入ws房间
        channelContextUtil.addMeetingRoom(meeting.getId(), user.getId());
        // 发送加入会议消息
        MeetingJoinDTO joinDTO = new MeetingJoinDTO();
        MeetingMember newMember = (MeetingMember) redisUtils.hGet(RedisCacheKey.getMeetingRoomKey(meeting.getId()), user.getId());
        joinDTO.setNewMember(newMember);
        List<MeetingMember> memberList = redisUtils.hValues(RedisCacheKey.getMeetingRoomKey(meeting.getId()));
        joinDTO.setMemberList(memberList);
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
        MeetingMember meetingMember = new MeetingMember();
        meetingMember.setMeetingId(meetingId);
        meetingMember.setUserId(userId);
        meetingMember.setNickname(nickName);
        meetingMember.setMemberType(memberType);
        meetingMember.setLastJoinDate(new Date());
        meetingMember.setStatus(MeetingMemberStatus.NORMAL.getCode());
        meetingMember.setMeetingStatus(MeetingStatus.RUNNING.getCode());
        meetingMemberMapper.insertOrUpdate(meetingMember);
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
    private void checkMemberJoin(String meetingId, String userId) {
        MeetingMemberDTO meetingMemberDTO = (MeetingMemberDTO) redisUtils.hGet(RedisCacheKey.getMeetingRoomKey(meetingId), userId);
        if (meetingMemberDTO != null && MeetingMemberStatus.BLACKLISTED.getCode().equals(meetingMemberDTO.getStatus())) {
            throw new BusinessException("用户已经被拉黑，不能加入会议");
        }
    }
}
