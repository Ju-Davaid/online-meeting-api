package api.meeting.service.impl;

import api.meeting.entity.po.MeetingMember;
import api.meeting.mapper.MeetingMemberMapper;
import api.meeting.service.MeetingMemberService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会议成员服务实现类
 */
@Service
public class MeetingMemberServiceImpl extends ServiceImpl<MeetingMemberMapper, MeetingMember> implements MeetingMemberService {
}
