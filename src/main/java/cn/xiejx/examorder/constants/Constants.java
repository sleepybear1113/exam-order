package cn.xiejx.examorder.constants;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.xiejx.examorder.entity.ReadPersonInfo;

import java.util.concurrent.TimeUnit;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/12 15:52
 */
public class Constants {
    public static Boolean isGui = true;

    public static final Cacher<String, ReadPersonInfo> READ_PERSON_INFO_CACHER = new CacherBuilder<String, ReadPersonInfo>().scheduleName("READ_PERSON_INFO_CACHER").delay(30, TimeUnit.SECONDS).build();

}
