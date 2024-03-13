package cn.sleepybear.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/07 15:56
 */
@Data
public class PersonInfo {
    public static final String OPTION_BLANK = "#选填";

    @ExcelIgnore
    private Integer id;

    @ExcelIgnore
    private String picSrc;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    /**
     * 准考证号
     */
    @ExcelProperty("考生号")
    private String examNumber;
    @ExcelProperty("准考证号")
    private String examNumber2;

    @ExcelProperty("类别代码")
    private String subjectType;

    @ExcelIgnore
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
    @ExcelProperty("证件号码")
    private String idCard;
    @ExcelProperty("身份证号")
    private String idCard2;

    @ExcelProperty("考点代码")
    private String examPlaceId;
    @ExcelIgnore
    private String examPlaceName;

    @ExcelProperty("所属学校")
    private String originExamPlaceName;
    @ExcelProperty("考试时间")
    private String time;

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
        this.subjectTypeName = SubjectEnum.getTypeName(subjectType);
    }

    public void setExamPlaceId(String examPlaceId) {
        if (StringUtils.isBlank(examPlaceId) || examPlaceId.startsWith(OPTION_BLANK)) {
            this.examPlaceId = null;
            return;
        }
        this.examPlaceId = examPlaceId;
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

    public void setIdCard2(String idCard2) {
        this.idCard2 = idCard2;
        if (StringUtils.isBlank(idCard)) {
            setIdCard(idCard2);
        }
    }

    public void setExamNumber2(String examNumber2) {
        this.examNumber2 = examNumber2;
        if (StringUtils.isBlank(this.examNumber)) {
            setExamNumber(examNumber2);
        }
    }

    public void setRoomNo(String roomNo) {
        if (StringUtils.isBlank(roomNo) || roomNo.startsWith(OPTION_BLANK)) {
            this.roomNo = null;
            return;
        }
        this.roomNo = roomNo;
    }

    public void setSeatNo(String seatNo) {
        if (StringUtils.isBlank(seatNo) || seatNo.startsWith(OPTION_BLANK)) {
            this.seatNo = null;
            return;
        }
        this.seatNo = seatNo;
    }

    public void buildPicSrc(Map<String, File> allPicMap) {
        File examNumberPic = allPicMap.get(this.examNumber);
        if (examNumberPic != null) {
            this.picSrc = examNumberPic.getAbsolutePath();
            return;
        }
        File idCardPicLowerCase = allPicMap.get(this.idCard.toLowerCase());
        File idCardPicUpperCase = allPicMap.get(this.idCard.toUpperCase());
        if (idCardPicLowerCase != null) {
            this.picSrc = idCardPicLowerCase.getAbsolutePath();
        }
        if (idCardPicUpperCase != null) {
            this.picSrc = idCardPicUpperCase.getAbsolutePath();
        }
    }

    public static void buildSeatNo(List<PersonInfo> list, String roomNo, Long random, String time) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        boolean resetSeatNo = random != null && random > 0;
        if (resetSeatNo) {
            Collections.shuffle(list, new Random(random));
        }

        String format = list.size() >= 100 ? "%03d" : "%02d";
        int index = 0;
        for (PersonInfo personInfo : list) {
            boolean reset = resetSeatNo || StringUtils.isBlank(personInfo.getSeatNo());
            personInfo.setSeatNo(reset ? String.format(format, (index + 1)) : personInfo.getSeatNo());
            personInfo.setRoomNo(roomNo);
            personInfo.setTime(time);
            if (reset) {
                index++;
            }
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
                res.add("第%s条数据：%s".formatted(i + 1, msg.toString()));
            }
        }
        return res;
    }

    public PersonInfo copy() {
        PersonInfo personInfo = new PersonInfo();
        BeanUtils.copyProperties(this, personInfo);
        return personInfo;
    }
}
