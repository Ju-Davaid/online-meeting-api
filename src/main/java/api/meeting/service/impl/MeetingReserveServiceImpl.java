package api.meeting.service.impl;

import api.meeting.entity.dto.CreateMeetingReserveDTO;
import api.meeting.entity.enums.MeetingReserveStatus;
import api.meeting.entity.po.MeetingReserve;
import api.meeting.entity.po.MeetingReserveMember;
import api.meeting.entity.po.User;
import api.meeting.exception.BusinessException;
import api.meeting.mapper.MeetingReserveMapper;
import api.meeting.mapper.MeetingReserveMemberMapper;
import api.meeting.service.MeetingReserveService;
import api.meeting.utils.JwtTokenUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议预约服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingReserveServiceImpl extends ServiceImpl<MeetingReserveMapper, MeetingReserve> implements MeetingReserveService {
    private final MeetingReserveMapper meetingReserveMapper;
    private final MeetingReserveMemberMapper meetingReserveMemberMapper;

    @Override
    public List<MeetingReserve> getMeetingReserves() {
        User user = JwtTokenUtils.getUserInfo();
        return meetingReserveMapper.selectList(new LambdaQueryWrapper<MeetingReserve>()
                .eq(MeetingReserve::getCreatedUserId, user.getId())
                .eq(MeetingReserve::getStatus, MeetingReserveStatus.UN_START.getCode())
                .orderByDesc(MeetingReserve::getCreatedTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMeetingReserve(CreateMeetingReserveDTO dto) {
        String meetingId = IdUtil.getSnowflakeNextIdStr();
        User user = JwtTokenUtils.getUserInfo();
        MeetingReserve meetingReserve = BeanUtil.copyProperties(dto, MeetingReserve.class);
        meetingReserve.setCreatedUserId(user.getId());
        meetingReserve.setMeetingId(meetingId);
        meetingReserve.setStatus(MeetingReserveStatus.UN_START.getCode());
        meetingReserve.setCreatedTime(new Date());
        // 插入会议预约记录
        meetingReserveMapper.insert(meetingReserve);
        // 插入会议预约成员
        if (!StrUtil.isBlankIfStr(dto.getInviteUserIds())) {
            String[] userIds = dto.getInviteUserIds().split(",");
            List<MeetingReserveMember> reserveMembers = Arrays.stream(userIds).map(userId -> {
                MeetingReserveMember meetingReserveMember = new MeetingReserveMember();
                meetingReserveMember.setMeetingId(meetingId);
                meetingReserveMember.setInviteUserId(userId);
                return meetingReserveMember;
            }).collect(Collectors.toCollection(ArrayList::new));
            MeetingReserveMember meetingReserveMember = new MeetingReserveMember();
            meetingReserveMember.setMeetingId(meetingId);
            meetingReserveMember.setInviteUserId(user.getId());
            reserveMembers.add(meetingReserveMember);
            meetingReserveMemberMapper.insert(reserveMembers);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMeetingReserve(String meetingId, String userId) {
        int count = meetingReserveMapper.delete(new LambdaQueryWrapper<MeetingReserve>()
                .eq(MeetingReserve::getMeetingId, meetingId)
                .eq(MeetingReserve::getCreatedUserId, userId)
        );
        if (count > 0) {
            meetingReserveMemberMapper.delete(new LambdaQueryWrapper<MeetingReserveMember>()
                    .eq(MeetingReserveMember::getMeetingId, meetingId)
            );
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMeetingReserveByUser(String meetingId, String userId) {
        MeetingReserve meetingReserve = meetingReserveMapper.selectById(meetingId);
        if (meetingReserve == null) {
            throw new BusinessException("会议预约不存在");
        }
        if (meetingReserve.getCreatedUserId().equals(userId)) {
            deleteMeetingReserve(meetingId, userId);
        } else {
            meetingReserveMemberMapper.delete(new LambdaQueryWrapper<MeetingReserveMember>()
                    .eq(MeetingReserveMember::getMeetingId, meetingId)
                    .eq(MeetingReserveMember::getInviteUserId, userId)
            );
        }
    }

    @Override
    public List<MeetingReserve> getTodayMeetingReserves() {
        Date now = new Date();
        Date beginOfToday = DateUtil.beginOfDay(now);
        Date endOfToday = DateUtil.endOfDay(now);
        User user = JwtTokenUtils.getUserInfo();
        return meetingReserveMapper.selectList(new LambdaQueryWrapper<MeetingReserve>()
                .eq(MeetingReserve::getCreatedUserId, user.getId())
                .eq(MeetingReserve::getStatus, MeetingReserveStatus.UN_START.getCode())
                .between(MeetingReserve::getStartTime, beginOfToday, endOfToday)
                .orderByDesc(MeetingReserve::getStartTime)
        );
    }
}
