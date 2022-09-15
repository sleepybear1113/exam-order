package cn.xjx.examorder;

import cn.xjx.examorder.utils.SpringContextUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xjx
 */
@SpringBootApplication
public class ExamOrderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExamOrderApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Override
    public void run(String... args) {
        Runnable runnable = () -> launch(args);
        new Thread(runnable).start();
    }

    public static void launch(String[] args) {
        FxmlApplication.launch(FxmlApplication.class, args);
    }
}
