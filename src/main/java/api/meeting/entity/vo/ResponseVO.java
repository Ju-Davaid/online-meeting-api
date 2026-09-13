package api.meeting.entity.vo;

import api.meeting.entity.enums.ResponseCodeEnum;
import lombok.Data;

/**
 * 响应VO
 *
 * @param <V> 响应数据类型
 */
@Data
public class ResponseVO<V> {
    private int code;
    private String msg;
    private V data;

    /**
     * 成功响应
     *
     * @param msg  成功信息
     * @param data 成功数据
     * @return 成功响应VO
     */
    public static <T> ResponseVO<T> success(String msg, T data) {
        ResponseVO<T> res = new ResponseVO<>();
        res.setCode(200);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

    /**
     * 成功响应
     *
     * @param msg 成功信息
     * @return 成功响应VO
     */
    public static ResponseVO<?> success(String msg) {
        ResponseVO<?> res = new ResponseVO<>();
        res.setCode(200);
        res.setMsg(msg);
        res.setData(null);
        return res;
    }

    /**
     * 错误响应
     *
     * @param msg 错误信息
     * @return 错误响应VO
     */
    public static <T> ResponseVO<T> error(String msg) {
        ResponseVO<T> res = new ResponseVO<>();
        res.setCode(400);
        res.setMsg(msg);
        res.setData(null);
        return res;
    }

    /**
     * 错误响应
     *
     * @param msg  错误信息
     * @param code 错误码
     * @return 错误响应VO
     */
    public static ResponseVO<?> error(String msg, Short code) {
        ResponseVO<?> res = new ResponseVO<>();
        res.setCode(code);
        res.setMsg(msg);
        res.setData(null);
        return res;
    }

    /**
     * 错误响应
     *
     * @param codeEnum 错误码枚举
     * @return 错误响应VO
     */
    public static <T> ResponseVO<T> error(ResponseCodeEnum codeEnum) {
        ResponseVO<T> res = new ResponseVO<>();
        res.setCode(codeEnum.getCode());
        res.setMsg(codeEnum.getMsg());
        res.setData(null);
        return res;
    }
}
