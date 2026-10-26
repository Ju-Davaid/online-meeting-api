package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户联系人申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserContactStatus {
    APPLYING(0, "申请中"),
    PASS(1, "通过"),
    REJECT(2, "拒绝"),
    BLACKED(3, "拉黑"),
    FRIENDED(4, "已成为好友"),
    DELETE(5, "已删除");


    private final Integer code;
    private final String desc;

    /**
     * 根据状态获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static UserContactStatus getByCode(Integer code) {
        for (UserContactStatus status : UserContactStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
