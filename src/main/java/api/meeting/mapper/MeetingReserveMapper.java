package api.meeting.mapper;

import api.meeting.entity.po.MeetingReserve;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议预约Mapper
 */
public interface MeetingReserveMapper extends BaseMapper<MeetingReserve> {
    List<MeetingReserve> getMeetingReserve(@Param("createdUserId") String createdUserId, @Param("status") Integer status);
}
