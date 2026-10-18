package api.meeting.entity.dto;

import lombok.Data;

/**
 * 信令连接数据DTO
 */
@Data
public class PeerConnectionDataDTO {
    private String token;
    private String sendUserId;
    private String receiveUserId;
    private String signalType;
    private String signalData;
}
