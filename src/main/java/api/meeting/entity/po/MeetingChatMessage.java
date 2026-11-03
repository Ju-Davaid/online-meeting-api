package api.meeting.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 会议聊天消息实体类
 */
@Data
public class MeetingChatMessage {
    @TableId
    private String id;
    private String meetingId;
    private Integer messageType;
    private String messageContent;
    private String sendUserId;
    private String receiveUserId;
    private Integer receiveType;
    private String sendUserNickname;
    private Date sendTime;
    private Long fileSize;
    private String fileName;
    private Integer fileType;
    private Integer status;
}