package cn.xjx.examorder.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
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

    /**
     * 准考证号
     */
    @ExcelProperty("准考证号")
    private String examNumber;


    @ExcelProperty("科目类别")
    private String subjectType;

    @ExcelProperty("科目类别名")
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

    @ExcelProperty("考点号")
    private String examPlaceId;
    @ExcelProperty("考点名称")
    private String examPlaceName;

    public void buildPicSrc(Map<String, File> allPicMap) {
        File examNumberPic = allPicMap.get(this.examNumber);
        if (examNumberPic != null) {
            this.picSrc = examNumberPic.getAbsolutePath();
            return;
        }
        File idCardPic = allPicMap.get(this.idCard);
        if (idCardPic != null) {
            this.picSrc = idCardPic.getAbsolutePath();
            return;
        }
    }

    public static void buildSeatNo(List<PersonInfo> list, String count) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        String format = list.size() >= 100 ? "%03d" : "%02d";
        for (int i = 0; i < list.size(); i++) {
            PersonInfo personInfo = list.get(i);
            personInfo.setSeatNo(String.format(format, (i + 1)));
            personInfo.setRoomNo(count);
        }
    }
}
