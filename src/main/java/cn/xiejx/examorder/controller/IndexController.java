package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.entity.*;
import cn.xiejx.examorder.fxml.IndexFxml;
import com.alibaba.excel.EasyExcel;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
        List<PersonInfo> personInfoListCopy = new ArrayList<>();
        for (PersonInfo personInfo : IndexFxml.personInfoList) {
            personInfoListCopy.add(personInfo.copy());
        }
        List<ExamPlaceInfo> examPlaceInfoList = ExamPlaceInfo.processPersonByGroup(personInfoListCopy, IndexFxml.SUBJECT_INFO_MAX_COUNT_LIST, random);
        allExamInfo.setList(examPlaceInfoList);
        allExamInfo.processPicSrc(IndexFxml.picPath);

        AllExamInfo res = new AllExamInfo();
        res.setList(allExamInfo.getList());
        return res;
    }

    @RequestMapping("/getSubjectInfoMaxCountList")
    public List<SubjectMaxCount> getSubjectInfoMaxCountList() {
        return IndexFxml.SUBJECT_INFO_MAX_COUNT_LIST;
    }

    @RequestMapping("/updateMaxCount")
    public Boolean updateMaxCount(@RequestBody List<SubjectMaxCount> list) {
        IndexFxml.SUBJECT_INFO_MAX_COUNT_LIST.clear();
        IndexFxml.SUBJECT_INFO_MAX_COUNT_LIST.addAll(list);
        SubjectMaxCount.write(list);
        return true;
    }

    @RequestMapping("/exportExcel")
    public Boolean exportExcel(String exportFileName) {
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
        fileChooser.setInitialFileName(StringUtils.isBlank(exportFileName) ? "核对单" : exportFileName);
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
