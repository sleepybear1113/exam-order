package cn.xjx.examorder.excel;

import cn.xjx.examorder.entity.PersonExamExcelListener;
import cn.xjx.examorder.entity.PersonInfo;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/07 16:39
 */
@Slf4j
public class Read {
    public static void main(String[] args) {
        String fileName = "11.xlsx";
        PersonExamExcelListener<PersonInfo> readListener = new PersonExamExcelListener<>();
        long t0 = System.currentTimeMillis();
        EasyExcel.read(fileName, PersonInfo.class, readListener).sheet().doRead();

        List<PersonInfo> data = readListener.getData();
        long t1 = System.currentTimeMillis();
        System.out.println("time:" + (t1 - t0) + " count:" + data.size());
    }

    public static List<PersonInfo> readData(String fileName) {
        PersonExamExcelListener<PersonInfo> readListener = new PersonExamExcelListener<>();
        EasyExcel.read(fileName, PersonInfo.class, readListener).sheet().doRead();
        return readListener.getData();
    }
}
