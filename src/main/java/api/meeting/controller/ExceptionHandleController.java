package api.meeting.controller;

import api.meeting.entity.enums.ResponseCodeEnum;
import api.meeting.entity.vo.ResponseVO;
import api.meeting.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理控制器
 */
@RestControllerAdvice
@Slf4j(topic = "ExceptionHandleController")
public class ExceptionHandleController {

    /**
     * 处理业务异常
     *
     * @param exception 业务异常
     * @param response  HTTP响应对象
     * @return 响应VO
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<?> handelBusinessException(BusinessException exception, HttpServletResponse response) {
        response.setStatus(exception.getCode());
        ResponseVO<?> res = new ResponseVO<>();
        res.setCode(exception.getCode());
        res.setMsg(exception.getMessage());
        res.setData(null);
        log.error("BusinessException: {}", exception.getMessage(), exception);
        return res;
    }

    /**
     * 处理参数验证异常
     *
     * @param exception 验证异常
     * @param response  HTTP响应对象
     * @return 响应VO
     */
    @ExceptionHandler(BindException.class)
    public ResponseVO<Map<String, String>> handelValidateException(BindException exception, HttpServletResponse response) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        ResponseCodeEnum responseCodeEnum = ResponseCodeEnum.ILLEGAL_PARAM;
        response.setStatus(responseCodeEnum.getCode());
        ResponseVO<Map<String, String>> res = new ResponseVO<>();
        res.setCode(responseCodeEnum.getCode());
        res.setMsg(responseCodeEnum.getMsg());
        res.setData(errors);
        log.error("BindException: {}", exception.getMessage(), exception);
        return res;
    }

    /**
     * 处理所有异常
     *
     * @param exception 异常
     * @param response  HTTP响应对象
     * @return 响应VO
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handelRuntimeException(Exception exception, HttpServletResponse response) {
        response.setStatus(500);
        ResponseVO<?> res = new ResponseVO<>();
        ResponseCodeEnum responseCodeEnum = ResponseCodeEnum.INTERNAL_SERVER_ERROR;
        res.setCode(responseCodeEnum.getCode());
        res.setMsg(responseCodeEnum.getMsg() + ":" + exception.getMessage());
        res.setData(null);
        log.error("Exception: {}", exception.getMessage(), exception);
        return res;
    }
}
