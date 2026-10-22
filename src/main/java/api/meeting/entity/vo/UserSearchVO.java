package api.meeting.entity.vo;

import lombok.Data;

/**
 * 用户搜索VO
 */
@Data
public class UserSearchVO {
    private String userId;
    private String nickName;
    private Integer status;
}
