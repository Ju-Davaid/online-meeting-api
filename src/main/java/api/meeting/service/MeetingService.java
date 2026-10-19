package api.meeting.service;

import api.meeting.entity.dto.PreJoinMeetingDTO;
import api.meeting.entity.dto.QuickMeetingDTO;
import api.meeting.entity.enums.MeetingMemberStatus;
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

    /**
     * 预加入会议
     *
     * @param preJoinMeetingDTO 预加入会议DTO
     * @return 会议ID
     */
    String preJoinMeeting(PreJoinMeetingDTO preJoinMeetingDTO);

    /**
     * 退出会议
     *
     * @param status 退出成员状态
     */
    void exitMeeting(MeetingMemberStatus status);

    /**
     * 强制退出会议
     *
     * @param targetUserId 目标用户ID
     * @param status       退出成员状态
     */
    void forceExitMeeting(String targetUserId, MeetingMemberStatus status);

    /**
     * 获取当前会议
     *
     * @return 当前会议
     */
    Meeting getCurrentMeeting();

    /**
     * 结束会议
     */
    void endMeeting();

    /**
     * 删除会议
     */
    void deleteMeeting();
}
