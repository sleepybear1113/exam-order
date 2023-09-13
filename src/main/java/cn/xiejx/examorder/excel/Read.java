package cn.xiejx.examorder.excel;

import cn.xiejx.examorder.entity.*;
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

    public static List<PersonInfo> readPersonData(FileStreamDto fileStreamDto) {
        PersonExamExcelListener<PersonInfo> readListener = new PersonExamExcelListener<>();
        EasyExcel.read(fileStreamDto.getByteArrayInputStream(), PersonInfo.class, readListener).sheet().doRead();
        return readListener.getData();
    }

    public static List<ExamRoomInfo> readRoomData(FileStreamDto fileStreamDto) {
        ExamRoomInfoExcelListener<ExamRoomInfo> readListener = new ExamRoomInfoExcelListener<>();
        EasyExcel.read(fileStreamDto.getByteArrayInputStream(), ExamRoomInfo.class, readListener).sheet().doRead();
        return ExamRoomInfo.buildIndex(readListener.getData());
    }
}
