package api.meeting.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    /**
     * 用户角色
     */
    USER(0, "USER"),
    /**
     * 管理员角色
     */
    ADMIN(1, "ADMIN");
    private final Integer code;
    private final String name;

    /**
     * 根据角色编码获取角色枚举
     *
     * @param code 角色编码
     * @return 角色枚举，如果未找到则返回null
     */
    public static UserRole getRoleByCode(Integer code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
