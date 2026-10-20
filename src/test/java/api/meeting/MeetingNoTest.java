package api.meeting;

import api.meeting.service.MeetingNoService;
import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MeetingNoTest {
    @Resource
    private MeetingNoService meetingNoService;

    @Test
    public void test() {
        Date beginOfToday = DateUtil.beginOfDay(new Date());
        Date endOfToday = DateUtil.endOfDay(new Date());
        System.out.println(beginOfToday);
        System.out.println(endOfToday);
//        System.out.println(meetingNoService.createUniqueMeetingCode());
    }
}
