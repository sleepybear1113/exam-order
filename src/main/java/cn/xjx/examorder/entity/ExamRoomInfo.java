package cn.xjx.examorder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer maxCount;
    private String roomName;
    private List<PersonInfo> list;

    public ExamRoomInfo(Integer maxCount, List<PersonInfo> list) {
        this.maxCount = maxCount;
        this.list = list;
    }
}
