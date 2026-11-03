package api.meeting;

import api.meeting.config.AppConfig;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    private AppConfig appConfig;

    @Test
    void contextLoads() {
        System.out.println(appConfig.getProjectLocation());
    }

}
