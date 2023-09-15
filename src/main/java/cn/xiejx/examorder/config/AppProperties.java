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

    @Value("${app.version}")
    private String version;
    @Value("${my-config.tmp-dir}")
    private String tmpDir;
    @Value("${my-config.export-tmp-dir}")
    private String exportTmpDir;
}
