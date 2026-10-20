package api.meeting.config;

import api.meeting.utils.MeetingNOGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 会议ID生成器配置类
 */
@Configuration
public class MeetingNoConfig {
    @Value("${meeting.id-generator.worker-id:0}")
    private long workerId;

    @Value("${meeting.id-generator.datacenter-id:0}")
    private long datacenterId;

    @Bean
    public MeetingNOGenerator meetingNOGenerator() {
        return new MeetingNOGenerator(workerId, datacenterId);
    }
}
