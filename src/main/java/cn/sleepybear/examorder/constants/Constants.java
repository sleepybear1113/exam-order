package cn.sleepybear.examorder.constants;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.sleepybear.examorder.entity.AllExamInfo;
import cn.sleepybear.examorder.entity.FileStreamDto;
import cn.sleepybear.examorder.entity.ReadPersonInfo;
import cn.sleepybear.examorder.entity.ReadRoomInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/12 15:52
 */
@Slf4j
public class Constants {
    public static final Cacher<String, ReadPersonInfo> READ_PERSON_INFO_CACHER = new CacherBuilder<String, ReadPersonInfo>().scheduleName("READ_PERSON_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, ReadRoomInfo> EXAM_ROOM_INFO_MAP_CACHER = new CacherBuilder<String, ReadRoomInfo>().scheduleName("EXAM_ROOM_INFO_MAP_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, AllExamInfo> ALL_EXAM_INFO_CACHER = new CacherBuilder<String, AllExamInfo>().scheduleName("ALL_EXAM_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, FileStreamDto> FILE_STREAM_DTO_CACHER = new CacherBuilder<String, FileStreamDto>().scheduleName("FILE_STREAM_DTO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();

    public static final Cacher<String, String> FILE_EXPORT_CACHER = new CacherBuilder<String, String>().scheduleName("FILE_EXPORT_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();

    public static final String PREFIX = "/api-exam-order";

    static {
        FILE_EXPORT_CACHER.setExpireAction((s, stringCacheObject, b) -> {
            String filename = stringCacheObject.getObjPure();
            if (b) {
                if (new File(filename).delete()) {
                    log.info("文件过期，删除文件成功：{}", filename);
                } else {
                    log.info("文件过期，删除文件失败：{}", filename);
                }
            }
        });
    }
}
