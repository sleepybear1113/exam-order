package cn.xjx.examorder.entity;

import cn.xjx.examorder.utils.Util;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/13 11:08
 */
@Data
@NoArgsConstructor
public class SubjectMaxCount implements Serializable {
    @Serial
    private static final long serialVersionUID = 2359316809951778098L;
    public static final int DEFAULT_MAC_COUNT = 30;

    private String subjectType;
    private String subjectTypeName;
    private Integer maxCount;

    public SubjectMaxCount(String subjectType, String subjectTypeName) {
        this.subjectType = subjectType;
        this.subjectTypeName = subjectTypeName;
    }

    public Integer getMaxCount() {
        return maxCount == null ? DEFAULT_MAC_COUNT : maxCount;
    }

    public static List<SubjectMaxCount> read() {
        List<SubjectMaxCount> list = Util.readFile("自定义配置.json", new TypeReference<>() {
        });
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static void write(List<SubjectMaxCount> list) {
        list.sort(Comparator.comparing(SubjectMaxCount::getSubjectType));
        Util.writeFile(list, "自定义配置.json");
    }

    public static void write(Map<String, SubjectMaxCount> list) {
        write(new ArrayList<>(list.values()));
    }
}
