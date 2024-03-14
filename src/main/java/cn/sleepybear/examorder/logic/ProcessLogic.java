package cn.sleepybear.examorder.logic;

import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.*;
import cn.sleepybear.examorder.excel.Read;
import cn.sleepybear.examorder.utils.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/16 10:03
 */
@Component
public class ProcessLogic {

    /**
     * 从 Excel 文件中读取人员信息
     *
     * @param fileStreamDto 文件流对象
     * @return 返回人员信息的 key 值
     */
    public static String readPersonExcel(FileStreamDto fileStreamDto) {
        List<PersonInfo> personInfoList = Read.readPersonData(fileStreamDto);
        List<String> valid = PersonInfo.valid(personInfoList);

        String key = Util.getRandomStr(8);
        ReadPersonInfo readPersonInfo = new ReadPersonInfo();
        readPersonInfo.setKey(key);
        readPersonInfo.setPersonInfoList(personInfoList);
        readPersonInfo.setPersonCount(CollectionUtils.size(personInfoList));
        readPersonInfo.setValidList(valid);

        Constants.READ_PERSON_INFO_CACHER.put(key, readPersonInfo);
        return key;
    }

    /**
     * 从 Excel 文件中读取考场信息
     *
     * @param fileStreamDto 文件流信息
     * @return 考场信息的 key 值
     */
    public static String readRoomExcel(FileStreamDto fileStreamDto) {
        // 从文件流信息中读取考场信息列表
        List<ExamRoomInfo> examRoomInfoInfoList = Read.readRoomData(fileStreamDto);

        // 如果考场信息列表为空，则返回空字符串
        if (CollectionUtils.isEmpty(examRoomInfoInfoList)) {
            return "";
        }

        // 遍历考场信息列表，清除无效的考场名称
        examRoomInfoInfoList.forEach(ExamRoomInfo::clearInvalidRoomName);

        // 生成随机的 key 值
        String key = Util.getRandomStr(8);
        // 创建用于存储考场信息的 HashMap
        HashMap<String, Map<String, List<ExamRoomInfo>>> map = new HashMap<>();

        // 遍历考场信息列表
        for (ExamRoomInfo examRoomInfo : examRoomInfoInfoList) {
            // 获取考场 ID 和科目类型
            String examPlaceId = examRoomInfo.getExamPlaceId();
            String subjectType = examRoomInfo.getSubjectType();

            // 获取指定考场 ID 的科目类型与考场信息列表的映射关系
            Map<String, List<ExamRoomInfo>> subjectExamRoomInfoMap = map.get(examPlaceId);

            // 如果映射关系为空，则创建新的映射关系，并添加考场信息到列表中
            if (subjectExamRoomInfoMap == null) {
                subjectExamRoomInfoMap = new HashMap<>();
                List<ExamRoomInfo> examRoomInfos = new ArrayList<>();
                examRoomInfos.add(examRoomInfo);
                subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                map.put(examPlaceId, subjectExamRoomInfoMap);
            } else {
                // 获取指定科目类型的考场信息列表
                List<ExamRoomInfo> examRoomInfos = subjectExamRoomInfoMap.get(subjectType);

                // 如果考场信息列表为空，则创建新的列表，并添加考场信息到列表中
                if (CollectionUtils.isEmpty(examRoomInfos)) {
                    examRoomInfos = new ArrayList<>();
                    examRoomInfos.add(examRoomInfo);
                    subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                } else {
                    // 如果考场信息列表不为空，则直接将考场信息添加到列表中
                    examRoomInfos.add(examRoomInfo);
                }
            }
        }

        // 从 HashMap 中构建 ReadRoomInfo 对象，并存储到缓存中，使用生成的 key 值作为缓存的 key
        Constants.EXAM_ROOM_INFO_MAP_CACHER.put(key, ReadRoomInfo.buildFromMap(key, map));

        // 返回生成的 key 值
        return key;
    }

}
