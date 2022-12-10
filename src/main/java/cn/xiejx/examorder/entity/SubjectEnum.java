package cn.xiejx.examorder.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/26 10:18
 */
public enum SubjectEnum {
    /**
     * 类别
     */
    机械类("机械类", "010"),
    计算机类("计算机类", "020"),
    文秘类("文秘类", "030"),
    化工类("化工类", "040"),
    药学类("药学类", "050"),
    建筑类("建筑类", "060"),
    烹饪类("烹饪类", "070"),
    旅游服务类("旅游服务类", "080"),
    服装类("服装类", "090"),
    财会类("财会类", "100"),
    电子与电工类("电子与电工类", "110"),
    商业类("商业类", "120"),
    外贸类("外贸类", "130"),
    医学护理类("医学护理类", "140"),
    农艺类("农艺类", "150"),
    艺术类("艺术类", "160"),
    工艺美术("工艺美术", "161"),
    影视表演("影视表演", "162"),
    舞蹈("舞蹈", "163"),
    音乐("音乐", "164"),
    时装表演("时装表演", "165"),
    其他类("其他类", "170"),
    安全防范("安全防范", "171"),
    体育("体育", "172"),
    学前教育("学前教育", "174"),
    汽车专业("汽车专业", "176"),
    ;

    private final String subjectType;
    private final String subjectTypeName;

    SubjectEnum(String subjectTypeName, String subjectType) {
        this.subjectType = subjectType;
        this.subjectTypeName = subjectTypeName;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getSubjectTypeName() {
        return subjectTypeName;
    }

    public static String getTypeName(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        for (SubjectEnum value : values()) {
            if (value.subjectType.equals(type)) {
                return value.subjectTypeName;
            }
        }

        return null;
    }
}
