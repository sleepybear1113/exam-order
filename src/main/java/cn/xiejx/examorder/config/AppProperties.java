package cn.xiejx.examorder.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author sleepybear
 */
@Getter
@Configuration
public class AppProperties {

    private String version;

    @Value("${app.version}")
    public void setVersion(String version) {
        this.version = version;
    }
}
