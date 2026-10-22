package api.meeting.controller;

import api.meeting.entity.po.UserContact;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.entity.vo.UserSearchVO;
import api.meeting.service.UserContactService;
import jakarta.validation.constraints.NotEmpty;
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

    public ResponseVO<UserContact> list() {
        return null;
    }

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

    @GetMapping("/apply")
    public ResponseVO<?> apply(String targetUserId) {
        userContactService.apply(targetUserId);
        return ResponseVO.success("申请成功");
    }
}
