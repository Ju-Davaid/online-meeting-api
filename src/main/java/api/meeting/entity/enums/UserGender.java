package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserGender {
    MALE(0, "男"),
    FEMALE(1, "女"),
    UNKNOWN(2, "未知");
    private final Integer code;
    private final String desc;
}
