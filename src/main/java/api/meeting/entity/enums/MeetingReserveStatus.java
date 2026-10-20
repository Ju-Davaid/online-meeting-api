package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingReserveStatus {
    UN_START(0, "未开始"),
    ENDED(1, "已结束");
    private final Integer code;
    private final String desc;
}
