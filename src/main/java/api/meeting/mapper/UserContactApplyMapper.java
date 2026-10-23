package api.meeting.mapper;

import api.meeting.entity.po.UserContactApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 用户联系人申请映射接口
 */
public interface UserContactApplyMapper extends BaseMapper<UserContactApply> {
    List<UserContactApply> getUserContactApplyList(@Param("userId") String userId);
}
