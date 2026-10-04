package api.meeting.service.impl;

import api.meeting.entity.po.Meeting;
import api.meeting.mapper.MeetingMapper;
import api.meeting.service.MeetingService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会议服务实现类
 */
@Service
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {
}
