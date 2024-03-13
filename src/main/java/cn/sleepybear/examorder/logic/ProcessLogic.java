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

    public static String readPersonExcel(FileStreamDto fileStreamDto) {
        List<PersonInfo> personInfoList = Read.readPersonData(fileStreamDto);
        List<String> valid = PersonInfo.valid(personInfoList);

        String key = Util.getRandomStr(8);
        ReadPersonInfo readPersonInfo = new ReadPersonInfo();
        readPersonInfo.setKey(key);
        readPersonInfo.setPersonInfoList(personInfoList);
        readPersonInfo.setValidList(valid);

        Constants.READ_PERSON_INFO_CACHER.put(key, readPersonInfo);
        return key;
    }

    public static String readRoomExcel(FileStreamDto fileStreamDto) {
        List<ExamRoomInfo> examRoomInfoInfoList = Read.readRoomData(fileStreamDto);

        if (CollectionUtils.isEmpty(examRoomInfoInfoList)) {
            return "";
        }
        examRoomInfoInfoList.forEach(ExamRoomInfo::clearInvalidRoomName);

        String key = Util.getRandomStr(8);
        HashMap<String, Map<String, List<ExamRoomInfo>>> map = new HashMap<>();
        Constants.EXAM_ROOM_INFO_MAP_CACHER.put(key, ReadRoomInfo.buildFromMap(key, map));

        for (ExamRoomInfo examRoomInfo : examRoomInfoInfoList) {
            String examPlaceId = examRoomInfo.getExamPlaceId();
            String subjectType = examRoomInfo.getSubjectType();

            Map<String, List<ExamRoomInfo>> subjectExamRoomInfoMap = map.get(examPlaceId);
            if (subjectExamRoomInfoMap == null) {
                subjectExamRoomInfoMap = new HashMap<>();
                List<ExamRoomInfo> examRoomInfos = new ArrayList<>();
                examRoomInfos.add(examRoomInfo);
                subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                map.put(examPlaceId, subjectExamRoomInfoMap);
            } else {
                List<ExamRoomInfo> examRoomInfos = subjectExamRoomInfoMap.get(subjectType);
                if (CollectionUtils.isEmpty(examRoomInfos)) {
                    examRoomInfos = new ArrayList<>();
                    examRoomInfos.add(examRoomInfo);
                    subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                } else {
                    examRoomInfos.add(examRoomInfo);
                }
            }
        }

        return key;
    }
}
