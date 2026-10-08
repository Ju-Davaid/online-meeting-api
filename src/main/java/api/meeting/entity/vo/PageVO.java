package api.meeting.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页VO
 */
@Data
public class PageVO<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<T> list;
}
