package api.meeting.entity.vo;

import java.util.List;

/**
 * 分页VO
 */
public class PageVO<T> {
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;
}
