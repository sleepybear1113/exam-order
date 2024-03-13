package cn.sleepybear.examorder.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/21 15:34
 */
@Data
public class ReadRoomInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8020379435832645698L;

    private String key;
    private Map<String, Map<String, List<ExamRoomInfo>>> mapMap;
    private Integer lines;
    private Integer examPlaceCount;
    private Integer examSubjectCount;
    private Integer examRoomCount;

    public ReadRoomInfo copy(boolean copyMapMap) {
        ReadRoomInfo readRoomInfo = new ReadRoomInfo();
        readRoomInfo.setKey(this.key);
        if (copyMapMap) {
            readRoomInfo.setMapMap(this.mapMap);
        }
        return readRoomInfo;
    }

    public static ReadRoomInfo buildFromMap(String key, Map<String, Map<String, List<ExamRoomInfo>>> mapMap) {
        ReadRoomInfo readRoomInfo = new ReadRoomInfo();
        readRoomInfo.setKey(key);
        readRoomInfo.setMapMap(mapMap);

        int totalRoomCount = 0;
        Set<String> placeIdSet = new HashSet<>();
        Set<String> subjectTypeSet = new HashSet<>();
        for (Map<String, List<ExamRoomInfo>> map : mapMap.values()) {
            for (List<ExamRoomInfo> examRoomInfos : map.values()) {
                for (ExamRoomInfo examRoomInfo : examRoomInfos) {
                    totalRoomCount += examRoomInfo.getRoomCount();
                    placeIdSet.add(examRoomInfo.getExamPlaceId());
                    subjectTypeSet.add(examRoomInfo.getSubjectType());
                }
            }
        }

        readRoomInfo.setLines(mapMap.size());
        readRoomInfo.setExamPlaceCount(placeIdSet.size());
        readRoomInfo.setExamSubjectCount(subjectTypeSet.size());
        readRoomInfo.setExamRoomCount(totalRoomCount);

        return readRoomInfo;
    }
}
