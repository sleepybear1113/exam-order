package cn.xjx.examorder.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/08 14:59
 */
@Data
@NoArgsConstructor
public class SubjectInfo {
    public static final int DEFAULT_MAC_COUNT = 30;

    private String subjectType;
    private String subjectTypeName;
    private Integer maxCount;

    private List<PersonInfo> personInfoList;
    private List<ExamRoomInfo> examRoomInfoList;

    public SubjectInfo(String subjectType, List<PersonInfo> personInfoList) {
        this(subjectType, null, personInfoList);
    }

    public SubjectInfo(String subjectType, String subjectTypeName, List<PersonInfo> personInfoList) {
        this.subjectType = subjectType;
        this.subjectTypeName = subjectTypeName;
        this.personInfoList = personInfoList;
    }

    public int buildExamRoomInfoList(int count, Long random) {
        if (CollectionUtils.isEmpty(personInfoList)) {
            return 0;
        }
        if (random != null && random > 0) {
            Collections.shuffle(personInfoList, new Random(random));
        }
        List<List<PersonInfo>> partition = ListUtils.partition(personInfoList, getMaxCount());
        examRoomInfoList = new ArrayList<>();
        for (List<PersonInfo> infoList : partition) {
            String roomNo = String.format("%04d", count);
            PersonInfo.buildSeatNo(infoList, roomNo);
            ExamRoomInfo examRoomInfo = new ExamRoomInfo(getMaxCount(), infoList);
            examRoomInfo.setRoomName(roomNo);
            count++;
            examRoomInfoList.add(examRoomInfo);
        }

        return partition.size();
    }

    public Integer getMaxCount() {
        return maxCount == null ? DEFAULT_MAC_COUNT : maxCount;
    }

    public static void sort(List<SubjectInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.sort(Comparator.comparing(SubjectInfo::getSubjectType));
    }
}
