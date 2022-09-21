package cn.xiejx.examorder.utils;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/02 21:45
 */
public class SpringContextUtil {
    private static ConfigurableApplicationContext applicationContext;

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext == null ? null : applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext == null ? null : getApplicationContext().getBean(name, clazz);
    }

}
