package cn.sleepybear.examorder.config;

import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/09 15:51
 */
@Configuration
public class FilePathConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(@Nonnull ResourceHandlerRegistry registry) {
        for (char c = 'A'; c <= 'Z'; c++) {
            registry.addResourceHandler(c + ":/**").addResourceLocations("file:" + c + ":/");
        }
        registry.addResourceHandler("/home/**").addResourceLocations("file:/home" + "/");
    }
}