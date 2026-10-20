package api.meeting.service;

import api.meeting.entity.dto.CreateMeetingReserveDTO;
import api.meeting.entity.po.MeetingReserve;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * 会议预约服务接口
 */
public interface MeetingReserveService extends IService<MeetingReserve> {
    /**
     * 获取会议预约
     */
    List<MeetingReserve> getMeetingReserves();

    /**
     * 创建会议预约
     *
     * @param dto 会议预约DTO
     */
    void createMeetingReserve(CreateMeetingReserveDTO dto);

    /**
     * 删除会议预约
     *
     * @param meetingId 会议预约ID不能为空
     * @param userId    用户ID不能为空z
     */
    void deleteMeetingReserve(String meetingId, String userId);

    /**
     * 删除用户预约的会议
     *
     * @param userId 用户ID不能为空
     */
    void deleteMeetingReserveByUser(String meetingId, String userId);

    /**
     * 获取今日会议预约
     *
     * @return 今日会议预约列表
     */
    List<MeetingReserve> getTodayMeetingReserves();
}
