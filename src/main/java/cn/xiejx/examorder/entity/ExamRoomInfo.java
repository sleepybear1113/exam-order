package cn.xiejx.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/09 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamRoomInfo {
    public static final String OPTION_BLANK = "#选填";

    @ExcelIgnore
    private Integer id;
    @ExcelProperty("考点代码")
    private String examPlaceId;
    @ExcelProperty("考点名称")
    private String examPlaceName;
    @ExcelProperty("类别代码")
    private String subjectType;
    @ExcelProperty("类别名称")
    private String subjectName;
    @ExcelProperty("容纳人数")
    private String maxCountStr;
    @ExcelIgnore
    private Integer maxCount;
    @ExcelProperty("试场数")
    private String roomCountStr;
    @ExcelIgnore
    private Integer roomCount;
    @ExcelProperty("试场号")
    private String roomName;
    @ExcelProperty("考试时间")
    private String time;
    @ExcelIgnore
    private List<List<PersonInfo>> list;

    public ExamRoomInfo(Integer maxCount, List<List<PersonInfo>> list) {
        this.maxCount = maxCount;
        this.list = list;
    }

    public void setExamPlaceId(String examPlaceId) {
        if (StringUtils.isBlank(examPlaceId) || examPlaceId.startsWith(OPTION_BLANK)) {
            this.examPlaceId = null;
            return;
        }
        this.examPlaceId = examPlaceId;
    }

    public void setMaxCountStr(String maxCount) {
        if (StringUtils.isBlank(maxCount) || maxCount.startsWith(OPTION_BLANK)) {
            this.maxCount = 30;
            return;
        }
        if (!StringUtils.isNumeric(maxCount)) {
            this.maxCount = 30;
            return;
        }
        this.maxCount = Integer.valueOf(maxCount);
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
        String typeName = SubjectEnum.getTypeName(subjectType);
        if (StringUtils.isBlank(typeName)) {
            return;
        }
        this.subjectName = typeName;
    }

    public void setSubjectName(String subjectName) {
        if (StringUtils.isBlank(subjectName) || subjectName.startsWith(OPTION_BLANK)) {
            if (StringUtils.isNotBlank(this.subjectName)) {
                return;
            }
        }
        this.subjectName = subjectName;
    }

    public String getMaxCountStr() {
        return String.valueOf(maxCount);
    }

    public void setRoomCountStr(String roomCount) {
        if (StringUtils.isBlank(roomCount) || roomCount.startsWith(OPTION_BLANK)) {
            this.roomCount = 100;
            return;
        }
        if (!StringUtils.isNumeric(roomCount)) {
            this.roomCount = 100;
            return;
        }
        this.roomCount = Integer.valueOf(roomCount);
    }

    public String getRoomCountStr() {
        return String.valueOf(roomCount);
    }

    public void setRoomName(String roomName) {
        if (StringUtils.isBlank(roomName) || roomName.startsWith(OPTION_BLANK)) {
            this.roomName = null;
            return;
        }
        this.roomName = roomName;
    }

    public void setTime(String time) {
        if (StringUtils.isBlank(time) || time.startsWith(OPTION_BLANK)) {
            this.time = null;
            return;
        }
        this.time = time;
    }

    public static List<ExamRoomInfo> buildIndex(List<ExamRoomInfo> examRoomInfoList) {
        if (CollectionUtils.isEmpty(examRoomInfoList)) {
            return examRoomInfoList;
        }
        int index = 0;
        for (ExamRoomInfo examRoomInfo : examRoomInfoList) {
            examRoomInfo.setId(++index);
        }
        return examRoomInfoList;
    }

    public static void buildExamRoom(List<ExamRoomInfo> examRoomInfoList, List<PersonInfo> personInfoList, Long random) {
        if (CollectionUtils.isEmpty(examRoomInfoList) || CollectionUtils.isEmpty(personInfoList)) {
            return;
        }

        // 是否重置座位号等
        boolean reset = random != null && random > 0;
        if (reset) {
            Collections.shuffle(personInfoList, new Random(random));
        }

        int startIndex = 0;
        for (ExamRoomInfo examRoomInfo : examRoomInfoList) {
            Integer roomCount = examRoomInfo.getRoomCount();
            Integer maxCount = examRoomInfo.getMaxCount();

            examRoomInfo.list = new ArrayList<>(roomCount);
            for (int i = 0; i < roomCount; i++) {
                if (startIndex >= personInfoList.size()) {
                    break;
                }
                List<PersonInfo> subList = personInfoList.subList(startIndex, Math.min(personInfoList.size(), startIndex + maxCount));
                examRoomInfo.list.add(new ArrayList<>(subList));
                startIndex += maxCount;
            }
        }
    }
}
