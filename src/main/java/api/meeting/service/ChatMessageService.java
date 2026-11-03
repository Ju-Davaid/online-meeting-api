package api.meeting.service;

import api.meeting.entity.dto.SendChatMessageDTO;
import api.meeting.entity.dto.UploadFileDTO;
import api.meeting.entity.po.MeetingChatMessage;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * 聊天消息服务接口
 */
public interface ChatMessageService extends IService<MeetingChatMessage> {
    /**
     * 获取聊天消息列表
     *
     * @param maxMessageId 最大消息ID
     * @param pageSize     分页大小
     * @return 分页结果集
     */
    List<MeetingChatMessage> getList(String maxMessageId, Integer pageSize);

    /**
     * 发送聊天消息
     *
     * @param message 发送聊天消息实体
     */
    void sendMessage(MeetingChatMessage message);

    /**
     * 上传文件
     *
     * @param dto 上传文件DTO
     */
    void uploadFile(UploadFileDTO dto);
}
