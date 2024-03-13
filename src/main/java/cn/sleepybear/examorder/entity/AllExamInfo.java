package cn.sleepybear.examorder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/09 15:09
 */
@Data
public class AllExamInfo {
    private String id;
    @JsonIgnore
    private List<ExamPlaceInfo> list;

    private List<Room> rooms;

    private List<PersonInfo> persons;
}
