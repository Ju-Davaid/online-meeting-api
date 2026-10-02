package api.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
public class MessageSendDTO<T> {
    private Integer messageSendType;
    private String meetingId;
    private Integer messageType;
    private String sendUserId;
    private String sendUserNickname;
    private T messageContent;
    private String receiveUserId;
    private Date sendTime;
    private String messageId;
    private Integer status;
    private String fileName;
    private Integer filePath;
    private String fileSize;
}
