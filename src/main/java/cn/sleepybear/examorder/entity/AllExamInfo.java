package cn.sleepybear.examorder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
