package cn.xjx.examorder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/08 14:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamPlaceInfo {
    private String examPlaceName;
    private List<SubjectInfo> list;

    public static List<ExamPlaceInfo> processPersonByGroup(List<PersonInfo> personInfoList, List<SubjectMaxCount> subjectInfoMaxCountList, Long random) {
        if (CollectionUtils.isEmpty(personInfoList)) {
            return new ArrayList<>();
        }
        Map<String, SubjectMaxCount> subjectInfoMaxCountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(subjectInfoMaxCountList)) {
            for (SubjectMaxCount subjectMaxCount : subjectInfoMaxCountList) {
                subjectInfoMaxCountMap.put(subjectMaxCount.getSubjectType(), subjectMaxCount);
            }
        }

        // 考点学校 map
        Map<String, List<PersonInfo>> personInfoExamPlaceNameMap = new HashMap<>();
        for (PersonInfo personInfo : personInfoList) {
            String examPlaceName = personInfo.getExamPlaceName();

            List<PersonInfo> infoList = personInfoExamPlaceNameMap.computeIfAbsent(examPlaceName, k -> new ArrayList<>());
            infoList.add(personInfo);
        }

        List<ExamPlaceInfo> examPlaceInfoList = new ArrayList<>();
        personInfoExamPlaceNameMap.forEach((s, personInfos) -> {
            // 遍历考点学校
            Map<String, List<PersonInfo>> subjectInfoMap = new HashMap<>();
            for (PersonInfo personInfo : personInfos) {
                String subjectType = personInfo.getSubjectType();
                List<PersonInfo> infoList = subjectInfoMap.computeIfAbsent(subjectType, k -> new ArrayList<>());
                infoList.add(personInfo);
            }

            // 某学校下，某学科全部学生
            List<SubjectInfo> list = new ArrayList<>();
            subjectInfoMap.forEach((subjectType, personList) -> {
                SubjectInfo subjectInfo = new SubjectInfo(subjectType, personList.get(0).getSubjectTypeName(), personList);
                SubjectMaxCount maxCountSubjectInfo = subjectInfoMaxCountMap.get(subjectType);
                subjectInfo.setMaxCount(maxCountSubjectInfo.getMaxCount());
                list.add(subjectInfo);
            });
            SubjectInfo.sort(list);

            // 第 n 个试场
            int count = 1;
            for (SubjectInfo subjectInfo : list) {
                // 随机学生考场
                int size = subjectInfo.buildExamRoomInfoList(count, random);
                count += size;
            }

            examPlaceInfoList.add(new ExamPlaceInfo(s, list));
        });

        return examPlaceInfoList;
    }

}
