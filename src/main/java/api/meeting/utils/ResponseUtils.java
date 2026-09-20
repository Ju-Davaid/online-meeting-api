package api.meeting.utils;

import api.meeting.entity.enums.ResponseCode;
import api.meeting.entity.vo.ResponseVO;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 */
public class ResponseUtils {
    /**
     * 写入错误响应
     *
     * @param response         HttpServletResponse
     * @param responseCodeEnum 响应码枚举
     * @throws IOException IOException
     */
    public static void writeErrorResponse(HttpServletResponse response, ResponseCode responseCodeEnum) throws IOException {
        response.setStatus(responseCodeEnum.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        ResponseVO<?> responseVO = new ResponseVO<>();
        responseVO.setCode(responseCodeEnum.getCode());
        responseVO.setMsg(responseCodeEnum.getMsg());
        JSONConfig jsonConfig = new JSONConfig();
        jsonConfig.setIgnoreNullValue(false);
        String json = JSONUtil.toJsonStr(responseVO, jsonConfig);
        response.getWriter().write(json);
    }
}
