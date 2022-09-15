package cn.xjx.examorder.controller;

import cn.xjx.examorder.entity.*;
import cn.xjx.examorder.fxml.IndexFxml;
import com.alibaba.excel.EasyExcel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/06 09:20
 */
@RestController
public class IndexController {
    private final AllExamInfo allExamInfo = new AllExamInfo();

    @RequestMapping("/processPersonByGroup")
    public AllExamInfo processPersonByGroup(Long random) {
        if (CollectionUtils.isEmpty(IndexFxml.personInfoList)) {
            return allExamInfo;
        }
        List<ExamPlaceInfo> examPlaceInfoList = ExamPlaceInfo.processPersonByGroup(IndexFxml.personInfoList, IndexFxml.subjectInfoMaxCountList, random);
        allExamInfo.setList(examPlaceInfoList);
        allExamInfo.processPicSrc(IndexFxml.picPath);
        allExamInfo.hidePersonInfo();

        AllExamInfo res = new AllExamInfo();
        res.setList(allExamInfo.getList());
        return res;
    }

    @RequestMapping("/getSubjectInfoMaxCountList")
    public List<SubjectMaxCount> getSubjectInfoMaxCountList() {
        return IndexFxml.subjectInfoMaxCountList;
    }

    @RequestMapping("/updateMaxCount")
    public Boolean updateMaxCount(@RequestBody List<SubjectMaxCount> list) {
        IndexFxml.subjectInfoMaxCountList.clear();
        IndexFxml.subjectInfoMaxCountList.addAll(list);
        SubjectMaxCount.write(list);
        return true;
    }

    @RequestMapping("/exportExcel")
    public Boolean exportExcel() {
        List<PersonInfo> data = new ArrayList<>();
        List<ExamPlaceInfo> list = allExamInfo.getList();
        for (ExamPlaceInfo examPlaceInfo : list) {
            for (SubjectInfo subjectInfo : examPlaceInfo.getList()) {
                for (ExamRoomInfo examRoomInfo : subjectInfo.getExamRoomInfoList()) {
                    data.addAll(examRoomInfo.getList());
                }
            }
        }

        String fileName = "simpleWrite-" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, PersonInfo.class)
                .sheet("模板")
                .doWrite(() -> {
                    // 分页查询数据
                    return data;
                });
        return true;
    }
}
