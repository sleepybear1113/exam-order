package cn.xiejx.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/07 15:56
 */
@Data
public class PersonInfo {
    @ExcelIgnore
    private Integer id;

    @ExcelIgnore
    private String picSrc;

    @ExcelProperty("姓名")
    private String name;

    private String sex;

    /**
     * 准考证号
     */
    @ExcelProperty("考生号")
    private String examNumber;


    @ExcelProperty("类别代码")
    private String subjectType;

    @ExcelProperty("类别名称")
    private String subjectTypeName;

    /**
     * 试场号
     */
    @ExcelProperty("试场号")
    private String roomNo;

    /**
     * 座位号
     */
    @ExcelProperty("座位号")
    private String seatNo;

    /**
     * 身份证号
     */
    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("考点代码")
    private String examPlaceId;
    @ExcelProperty("考点名称")
    private String examPlaceName;

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
        this.subjectTypeName = SubjectEnum.getTypeName(subjectType);
    }

    public void setSubjectTypeName(String subjectTypeName) {
        if (StringUtils.isNotBlank(subjectTypeName)) {
            this.subjectTypeName = subjectTypeName;
        }
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
        this.sex = "";
        if (StringUtils.isNotBlank(idCard) && idCard.length() > 16) {
            int c = idCard.charAt(16) - '0';
            if (c >= 0 && c <= 9) {
                this.sex = c % 2 == 1 ? "男" : "女";
            }
        }
    }

    public void buildPicSrc(Map<String, File> allPicMap) {
        File examNumberPic = allPicMap.get(this.examNumber);
        if (examNumberPic != null) {
            this.picSrc = examNumberPic.getAbsolutePath();
            return;
        }
        File idCardPic = allPicMap.get(this.idCard);
        if (idCardPic != null) {
            this.picSrc = idCardPic.getAbsolutePath();
        }
    }

    public static void buildSeatNo(List<PersonInfo> list, String roomNo, Long random) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        boolean resetSeatNo = random != null && random > 0;
        if (!resetSeatNo) {
            for (PersonInfo personInfo : list) {
                if (StringUtils.isBlank(personInfo.getSeatNo())) {
                    resetSeatNo = true;
                    break;
                }
            }
        }

        String format = list.size() >= 100 ? "%03d" : "%02d";
        for (int i = 0; i < list.size(); i++) {
            PersonInfo personInfo = list.get(i);
            personInfo.setSeatNo(resetSeatNo ? String.format(format, (i + 1)) : personInfo.getSeatNo());
            personInfo.setRoomNo(roomNo);
        }
    }

    public static List<String> valid(List<PersonInfo> list) {
        List<String> res = new ArrayList<>();
        res.add("true");
        if (CollectionUtils.isEmpty(list)) {
            res.set(0, "false");
            res.add("未读取到数据");
            return res;
        }

        int size = list.size();
        list.removeIf(personInfo -> StringUtils.isBlank(personInfo.getName()) && StringUtils.isBlank(personInfo.getExamNumber()) && StringUtils.isBlank(personInfo.getSubjectType()));
        int subSize = size - list.size();
        if (subSize > 0) {
            res.add("空行数据数量：%s，已经跳过".formatted(subSize));
        }

        for (int i = 0; i < list.size(); i++) {
            PersonInfo personInfo = list.get(i);
            StringBuilder msg = new StringBuilder();
            if (StringUtils.isBlank(personInfo.getName())) {
                res.set(0, "false");
                msg.append("没有姓名；");
            }
            if (StringUtils.isBlank(personInfo.getExamNumber())) {
                res.set(0, "false");
                msg.append("没有考生号；");
            }
            if (StringUtils.isBlank(personInfo.getSubjectType())) {
                res.set(0, "false");
                msg.append("没有类别代码；");
            }
            if (!msg.isEmpty()) {
                res.add("第%s条数据：%s".formatted(i, msg.toString()));
            }
        }
        return res;
    }
}
