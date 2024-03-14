package cn.sleepybear.examorder.constants;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.sleepybear.examorder.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 一些常量或者全局变量
 *
 * @author sleepybear
 * @date 2023/09/12 15:52
 */
@Slf4j
public class Constants {
    public static final String PREFIX = "/api-exam-order";

    public static final Cacher<String, ReadPersonInfo> READ_PERSON_INFO_CACHER = new CacherBuilder<String, ReadPersonInfo>().scheduleName("READ_PERSON_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, ReadRoomInfo> EXAM_ROOM_INFO_MAP_CACHER = new CacherBuilder<String, ReadRoomInfo>().scheduleName("EXAM_ROOM_INFO_MAP_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, AllExamInfo> ALL_EXAM_INFO_CACHER = new CacherBuilder<String, AllExamInfo>().scheduleName("ALL_EXAM_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, FileStreamDto> FILE_STREAM_DTO_CACHER = new CacherBuilder<String, FileStreamDto>().scheduleName("FILE_STREAM_DTO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();

    /**
     * 文件字节缓存，用于导出文件
     */
    public static final Cacher<String, FileBytes> FILE_BYTES_EXPORT_CACHER = new CacherBuilder<String, FileBytes>().scheduleName("FILE_BYTES_EXPORT_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();

}
