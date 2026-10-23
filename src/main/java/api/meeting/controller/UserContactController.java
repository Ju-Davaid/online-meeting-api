package api.meeting.controller;

import api.meeting.entity.po.UserContact;
import api.meeting.entity.po.UserContactApply;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.entity.vo.UserSearchVO;
import api.meeting.service.UserContactService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
public class UserContactController {
    private final UserContactService userContactService;

    /**
     * 搜索用户联系人
     *
     * @param userId 用户ID
     * @return 用户搜索VO
     */
    @GetMapping("/search")
    public ResponseVO<UserSearchVO> search(@NotEmpty(message = "用户ID不能为空") String userId) {
        return ResponseVO.success("搜索成功", userContactService.searchUser(userId));
    }

    /**
     * 申请好友
     *
     * @param targetUserId 目标用户ID
     * @return 申请状态
     */
    @GetMapping("/apply")
    public ResponseVO<?> apply(@NotEmpty(message = "目标用户ID不能为空") String targetUserId) {
        return ResponseVO.success("申请成功", userContactService.apply(targetUserId));
    }

    /**
     * 处理好友申请
     *
     * @param targetUserId 目标用户ID
     * @param status  申请状态
     * @return 处理状态
     */
    @GetMapping("/handle")
    public ResponseVO<?> handle(@NotEmpty(message = "目标用户ID不能为空") String targetUserId, @NotNull(message = "申请状态不能为空") Integer status) {
        userContactService.handleApply(targetUserId, status);
        return ResponseVO.success("处理成功");
    }

    /**
     * 获取用户好友列表
     *
     * @return 好友列表
     */
    @GetMapping("/list")
    public ResponseVO<List<UserContact>> list() {
        return ResponseVO.success("获取成功", userContactService.getUserContactList());
    }

    @GetMapping("/apply/list")
    public ResponseVO<List<UserContactApply>> applyList() {
        return ResponseVO.success("获取成功", userContactService.getUserContactApplyList());
    }
}
