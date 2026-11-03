package api.meeting.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String projectLocation;
    private String staticLocation;
    private String tempLocation;

    @PostConstruct
    private void init() {
        this.staticLocation = projectLocation + staticLocation;
        this.tempLocation = projectLocation + tempLocation;
    }
}
