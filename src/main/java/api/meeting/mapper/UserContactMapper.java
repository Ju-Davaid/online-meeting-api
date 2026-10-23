package api.meeting.mapper;

import api.meeting.entity.po.UserContact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户联系人映射接口
 */
public interface UserContactMapper extends BaseMapper<UserContact> {
    List<UserContact> getUserContactList(@Param("userId") String userId);
}
