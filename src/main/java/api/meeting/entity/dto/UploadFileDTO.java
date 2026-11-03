package api.meeting.entity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 上传文件DTO
 */
@Data
public class UploadFileDTO {
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
    @NotEmpty(message = "消息ID不能为空")
    private String messageId;
    @NotNull(message = "发送时间不能为空")
    private Date sendTime;
}
