package cn.xiejx.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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

    @ExcelProperty("考点代码")
    private String examPlaceId;
    @ExcelProperty("类别代码")
    private String subjectType;
    @ExcelProperty("容纳人数")
    private Integer maxCount;
    @ExcelProperty("试场数")
    private Integer roomCount;
    @ExcelProperty("试场号")
    private String roomName;
    @ExcelProperty("考试时间")
    private String time;
    @ExcelIgnore
    private List<PersonInfo> list;

    public ExamRoomInfo(Integer maxCount, List<PersonInfo> list) {
        this.maxCount = maxCount;
        this.list = list;
    }

    public void setExamPlaceId(String examPlaceId) {
        if (StringUtils.isNotBlank(examPlaceId) && examPlaceId.startsWith(OPTION_BLANK)) {
            return;
        }
        this.examPlaceId = examPlaceId;
    }

    public void setMaxCount(String maxCount) {
        if (StringUtils.isNotBlank(maxCount) && maxCount.startsWith(OPTION_BLANK)) {
            this.maxCount = 30;
            return;
        }
        if (!StringUtils.isNumeric(maxCount)) {
            this.maxCount = 30;
            return;
        }
        this.maxCount = Integer.valueOf(maxCount);
    }

    public void setRoomCount(String roomCount) {
        if (StringUtils.isNotBlank(roomCount) && roomCount.startsWith(OPTION_BLANK)) {
            this.roomCount = 100;
            return;
        }
        if (!StringUtils.isNumeric(roomCount)) {
            this.roomCount = 100;
            return;
        }
        this.roomCount = Integer.valueOf(roomCount);
    }

    public void setRoomName(String roomName) {
        if (StringUtils.isNotBlank(roomName) && roomName.startsWith(OPTION_BLANK)) {
            return;
        }
        this.roomName = roomName;
    }

    public void setTime(String time) {
        if (StringUtils.isNotBlank(time) && time.startsWith(OPTION_BLANK)) {
            return;
        }
        this.time = time;
    }
}
