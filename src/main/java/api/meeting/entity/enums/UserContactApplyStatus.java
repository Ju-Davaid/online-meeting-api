package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户联系人申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserContactApplyStatus {
    APPLYING(0, "申请中"),
    PASS(1, "通过"),
    REJECT(2, "拒绝"),
    BLACKED(3, "拉黑"),
    FRIENDED(4, "已成为好友");


    private final Integer code;
    private final String desc;
}
