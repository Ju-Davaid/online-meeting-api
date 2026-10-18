package api.meeting.entity.dto;

import lombok.Data;

/**
 * 信令消息DTO
 */
@Data
public class PeerMessageDTO {
    private String signalType;
    private String signalData;
}
