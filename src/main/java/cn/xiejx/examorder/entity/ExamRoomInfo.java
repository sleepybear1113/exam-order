package cn.xiejx.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;

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
    private Boolean detail = false;
    @ExcelIgnore
    private List<List<PersonInfo>> list = new ArrayList<>();
    @ExcelIgnore
    private List<PersonInfo> persons = new ArrayList<>();
    @ExcelIgnore
    private List<ExamRoomInfo> roomList = new ArrayList<>();

    @ExcelIgnore
    private List<String> roomNoList = new ArrayList<>();

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
        this.roomNoList = List.of(roomName.replace("，", ",").split(","));
    }

    public void clearInvalidRoomName() {
        if (CollectionUtils.isEmpty(this.roomNoList) || this.roomCount == null) {
            return;
        }
        this.roomNoList = new ArrayList<>(new HashSet<>(this.roomNoList));
        if (this.roomNoList.size() == this.roomCount) {
            return;
        }
        if (this.roomCount > this.roomNoList.size()) {
            this.roomNoList = new ArrayList<>();
        } else {
            this.roomNoList = new ArrayList<>(this.roomNoList.subList(0, this.roomCount));
        }
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

    public static int buildExamRoom(List<ExamRoomInfo> examRoomInfoList, List<PersonInfo> personInfoList, int roomIndex, Long random) {
        if (CollectionUtils.isEmpty(examRoomInfoList) || CollectionUtils.isEmpty(personInfoList)) {
            return roomIndex;
        }

        // 是否重置座位号等
        boolean reset = random != null && random > 0;
        if (reset) {
            // 打乱顺序
            Collections.shuffle(personInfoList, new Random(random));
        }

        // 试场 map，若指定了试场名，那么放入 map
        Map<String, List<PersonInfo>> roomPersonMap = new HashMap<>();
        for (ExamRoomInfo examRoomInfo : examRoomInfoList) {
            if (StringUtils.isBlank(examRoomInfo.getRoomName())) {
                continue;
            }
            roomPersonMap.put(examRoomInfo.getRoomName(), new ArrayList<>());
        }

        // 学生信息有试场号的放入上面 map，并且移除
        personInfoList.removeIf(personInfo -> {
            String roomNo = personInfo.getRoomNo();
            if (StringUtils.isBlank(roomNo)) {
                return false;
            }
            List<PersonInfo> personInfos = roomPersonMap.get(roomNo);
            if (personInfos == null) {
                return false;
            }
            personInfos.add(personInfo);
            return true;
        });

        // 遍历所有科目的试场
        int startIndex = 0;
        for (ExamRoomInfo examRoomInfo : examRoomInfoList) {
            // 遍历该科目前所有试场
            for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                String name = roomInfo.getRoomName();
                Integer maxCount = roomInfo.getMaxCount();

                // 将特定试场的先装入
                List<PersonInfo> personInfos = roomPersonMap.get(name);
                if (CollectionUtils.isNotEmpty(personInfos)) {
                    roomInfo.getPersons().addAll(personInfos.subList(0, Math.min(personInfos.size(), maxCount)));
                    roomInfo.getPersons().forEach(personInfo -> personInfo.setTime(roomInfo.getTime()));
                }
            }

            if (CollectionUtils.isEmpty(personInfoList)) {
                continue;
            }

            // 如果有剩余未安排的学生，把学生放入试场
            for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                if (startIndex >= personInfoList.size()) {
                    break;
                }

                int leftCount = roomInfo.getMaxCount() - roomInfo.getPersons().size();
                if (leftCount <= 0) {
                    continue;
                }
                List<PersonInfo> subList = new ArrayList<>(personInfoList.subList(startIndex, Math.min(personInfoList.size(), startIndex + leftCount)));
                roomInfo.getPersons().addAll(subList);
                String roomNo = StringUtils.isBlank(roomInfo.getRoomName()) ? "%03d".formatted(roomIndex++) : roomInfo.getRoomName();
                // 构建座位号
                PersonInfo.buildSeatNo(roomInfo.getPersons(), roomNo, random, examRoomInfo.getTime());
                roomInfo.getPersons().forEach(personInfo -> personInfo.setExamPlaceName(examRoomInfo.getExamPlaceName()));
                roomInfo.setRoomName(roomNo);
                startIndex += leftCount;
            }
        }

        return roomIndex;
    }

    public void buildDetail() {
        if (this.roomCount == null || this.roomCount <= 0) {
            return;
        }
        this.roomList = new ArrayList<>();
        ExamRoomInfo copy = new ExamRoomInfo();
        BeanUtils.copyProperties(this, copy);

        for (int i = 0; i < this.roomCount; i++) {
            ExamRoomInfo e = new ExamRoomInfo();
            BeanUtils.copyProperties(copy, e);
            e.setPersons(new ArrayList<>());
            e.setDetail(true);
            e.setRoomNoList(new ArrayList<>(copy.getRoomNoList()));
            e.setRoomList(null);
            if (CollectionUtils.isNotEmpty(this.roomNoList)) {
                e.setRoomName(copy.getRoomNoList().get(i));
            }
            this.roomList.add(e);
        }
    }
}
