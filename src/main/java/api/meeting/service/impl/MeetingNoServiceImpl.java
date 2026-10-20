package api.meeting.service.impl;

import api.meeting.service.MeetingNoService;
import api.meeting.utils.MeetingNOGenerator;
import api.meeting.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingNoServiceImpl implements MeetingNoService {
    private final MeetingNOGenerator meetingNOGenerator;
    private final RedisUtils<String> redisUtils;
    private static final String MEETING_CODE_KEY_PREFIX = "meeting:no:";
    private static final int MAX_RETRY = 10;

    @Override
    public long createUniqueMeetingCode() {
        for (int i = 0; i < MAX_RETRY; i++) {
            long candidate = meetingNOGenerator.generateMeetingCode();

            Boolean success = redisUtils.setIfAbsent(
                    MEETING_CODE_KEY_PREFIX + candidate,
                    "1",
                    30, TimeUnit.DAYS
            );
            if (Boolean.TRUE.equals(success)) {
                log.info("成功生成会议号：{}", candidate);
                return candidate;
            }
        }
        throw new IllegalStateException("生成会议号失败，重试次数已用完");
    }
}
