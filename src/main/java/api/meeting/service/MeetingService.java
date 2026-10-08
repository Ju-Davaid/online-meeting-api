package api.meeting.service;

import api.meeting.entity.dto.QuickMeetingDTO;
import api.meeting.entity.po.Meeting;
import api.meeting.entity.vo.PageVO;
import api.meeting.entity.vo.ResponseVO;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * 会议服务接口
 */
public interface MeetingService extends IService<Meeting> {

    /**
     * 获取会议列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 会议列表
     */
    PageVO<Meeting> getMeetingList(Integer pageNum, Integer pageSize);

    /**
     * 快速创建会议
     *
     * @param quickMeetingDTO 快速创建会议DTO
     */
    void quickMeeting(QuickMeetingDTO quickMeetingDTO);

    /**
     * 加入会议
     *
     * @param videoOpen 是否开启视频
     */
    void joinMeeting(boolean videoOpen);
}
