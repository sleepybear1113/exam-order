package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.entity.*;
import cn.xiejx.examorder.fxml.IndexFxml;
import com.alibaba.excel.EasyExcel;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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
        allExamInfo.setList(examPlaceInfoList.subList(0, 2));
        allExamInfo.processPicSrc(IndexFxml.picPath);

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
        if (CollectionUtils.isEmpty(data)) {
            return false;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("xlsx", "*.xlsx")
        );

        Platform.runLater(()-> {
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null) {
                return;
            }
            String absolutePath = file.getAbsolutePath();

            EasyExcel.write(absolutePath, PersonInfo.class)
                    .sheet("模板")
                    .doWrite(() -> data);
        });

        return true;
    }
}
