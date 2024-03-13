package cn.sleepybear.examorder;

import cn.sleepybear.examorder.utils.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xjx
 */
@SpringBootApplication
public class ExamOrderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExamOrderApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}
