package cn.xiejx.examorder.constants;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.xiejx.examorder.entity.AllExamInfo;
import cn.xiejx.examorder.entity.ExamRoomInfo;
import cn.xiejx.examorder.entity.FileStreamDto;
import cn.xiejx.examorder.entity.ReadPersonInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/12 15:52
 */
public class Constants {
    public static Boolean isGui = true;

    public static final Cacher<String, ReadPersonInfo> READ_PERSON_INFO_CACHER = new CacherBuilder<String, ReadPersonInfo>().scheduleName("READ_PERSON_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, Map<String, Map<String, List<ExamRoomInfo>>>> EXAM_ROOM_INFO_MAP_CACHER = new CacherBuilder<String, Map<String, Map<String, List<ExamRoomInfo>>>>().scheduleName("EXAM_ROOM_INFO_MAP_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, AllExamInfo> ALL_EXAM_INFO_CACHER = new CacherBuilder<String, AllExamInfo>().scheduleName("ALL_EXAM_INFO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();
    public static final Cacher<String, FileStreamDto> FILE_STREAM_DTO_CACHER = new CacherBuilder<String, FileStreamDto>().scheduleName("FILE_STREAM_DTO_CACHER").delay(30, TimeUnit.SECONDS).allowNullKey(String.valueOf(System.currentTimeMillis())).build();

    public static final String PREFIX = "/api-exam-order";
}
