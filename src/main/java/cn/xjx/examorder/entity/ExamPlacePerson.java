package cn.xjx.examorder.entity;

import lombok.Data;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/08 14:32
 */
@Data
public class ExamPlacePerson {
    private Integer maxCount;
    private List<PersonInfo> list;
}
