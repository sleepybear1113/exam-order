package cn.xiejx.examorder;

import cn.xiejx.examorder.utils.SpringContextUtil;
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
        for (String arg : args) {
            if ("disable-fx".equals(arg)) {
                System.out.println("禁用JavaFx, 将以命令行模式运行");
                return;
            }
        }
        Runnable runnable = () -> launch(args);
        new Thread(runnable).start();
    }

    public static void launch(String[] args) {
        FxmlApplication.launch(FxmlApplication.class, args);
    }
}
