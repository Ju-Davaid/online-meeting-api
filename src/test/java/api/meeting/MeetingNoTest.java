package api.meeting;

import api.meeting.service.MeetingNoService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MeetingNoTest {
    @Resource
    private MeetingNoService meetingNoService;

    @Test
    public void test() {
        System.out.println(meetingNoService.createUniqueMeetingCode());
    }
}
