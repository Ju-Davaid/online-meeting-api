package api.meeting.exception;

import api.meeting.entity.enums.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

/**
 * 业务异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    int code;

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public BusinessException(ResponseCode status) {
        super(status.getMsg());
        this.code = status.getCode();
    }

}
