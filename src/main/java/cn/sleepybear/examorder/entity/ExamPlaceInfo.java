package cn.sleepybear.examorder.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/08 14:36
 */
@Data
@NoArgsConstructor
public class ExamPlaceInfo {
    private String examPlaceName;
    private String examPlaceId;

    private List<ExamRoomInfo> list;

    public ExamPlaceInfo(String examPlaceName, String examPlaceId, List<ExamRoomInfo> list) {
        this.examPlaceName = examPlaceName;
        this.examPlaceId = examPlaceId;
        this.list = list;
    }

    public static List<ExamPlaceInfo> processPersonByGroup(List<PersonInfo> personInfoList, Map<String, Map<String, List<ExamRoomInfo>>> placeSubjectRoomInfoMap, Long random) {
        if (CollectionUtils.isEmpty(personInfoList)) {
            return new ArrayList<>();
        }

        // 考点学校-学生信息 map
        Map<String, List<PersonInfo>> personInfoExamPlaceNameMap = new HashMap<>();
        for (PersonInfo personInfo : personInfoList) {
            String examPlaceId = personInfo.getExamPlaceId();

            List<PersonInfo> infoList = personInfoExamPlaceNameMap.computeIfAbsent(examPlaceId, k -> new ArrayList<>());
            infoList.add(personInfo);
        }

        List<ExamPlaceInfo> examPlaceInfoList = new ArrayList<>();
        final int[] roomIndex = {1};
        personInfoExamPlaceNameMap.forEach((examPlaceId, personInfos) -> {
            // 遍历考点学校
            Map<String, List<PersonInfo>> subjectPersonInfoMap = new HashMap<>();
            for (PersonInfo personInfo : personInfos) {
                String subjectType = personInfo.getSubjectType();
                List<PersonInfo> infoList = subjectPersonInfoMap.computeIfAbsent(subjectType, k -> new ArrayList<>());
                infoList.add(personInfo);
            }

            Map<String, List<ExamRoomInfo>> subjectExamRoomInfoMap = placeSubjectRoomInfoMap.get(examPlaceId);
            if (subjectExamRoomInfoMap == null) {
                examPlaceId = null;
                subjectExamRoomInfoMap = placeSubjectRoomInfoMap.get(null);
            }

            if (MapUtils.isEmpty(subjectExamRoomInfoMap)) {
                return;
            }

            // 某个科目下的学生
            for (String subject : subjectExamRoomInfoMap.keySet()) {
                List<ExamRoomInfo> examRoomInfos = subjectExamRoomInfoMap.get(subject);
                for (ExamRoomInfo examRoomInfo : examRoomInfos) {
                    if (examRoomInfo.getExamPlaceId() == null && examPlaceId != null) {
                        examRoomInfo.setExamPlaceId(examPlaceId);
                    }
                    examRoomInfo.buildDetail();
                }
                List<PersonInfo> personList = subjectPersonInfoMap.get(subject);
                roomIndex[0] = ExamRoomInfo.buildExamRoom(examRoomInfos, personList, roomIndex[0], random);

                String examPlaceName = examRoomInfos.get(0).getExamPlaceName();
                examPlaceInfoList.add(new ExamPlaceInfo(examPlaceName, examPlaceId, examRoomInfos));
            }
        });

        List<ExamPlaceInfo> res = new ArrayList<>();
        for (ExamPlaceInfo examPlaceInfo : examPlaceInfoList) {
            ExamPlaceInfo copy = new ExamPlaceInfo();
            BeanUtils.copyProperties(examPlaceInfo, copy);
            res.add(copy);
        }


        for (ExamPlaceInfo examPlaceInfo : res) {
            // 遍历多个考点

            List<ExamRoomInfo> list = new ArrayList<>();
            for (ExamRoomInfo examRoomInfo : examPlaceInfo.getList()) {
                // 遍历考点下的多个科目试场
                List<ExamRoomInfo> personRoomList = new ArrayList<>();
                for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                    if (CollectionUtils.isNotEmpty(roomInfo.getPersons())) {
                        personRoomList.add(roomInfo);
                    }
                }
                if (CollectionUtils.isNotEmpty(personRoomList)) {
                    examRoomInfo.setRoomList(personRoomList);
                    list.add(examRoomInfo);
                }
            }
            examPlaceInfo.setList(list);
            examPlaceInfo.getList().removeIf(examRoomInfo -> CollectionUtils.isEmpty(examRoomInfo.getRoomList()));
        }

        res.removeIf(examPlaceInfo -> CollectionUtils.isEmpty(examPlaceInfo.getList()));
        res.sort(Comparator.comparingInt(o -> o.getList().get(0).getId()));
        return res;
    }

}
