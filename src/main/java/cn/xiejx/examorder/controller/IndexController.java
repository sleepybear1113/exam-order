package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.entity.AllExamInfo;
import cn.xiejx.examorder.entity.ExamPlaceInfo;
import cn.xiejx.examorder.entity.ExamRoomInfo;
import cn.xiejx.examorder.entity.PersonInfo;
import cn.xiejx.examorder.fxml.IndexFxml;
import com.alibaba.excel.EasyExcel;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.*;

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

        IndexFxml.placeSubjectRoomInfoMap.values().forEach(map -> map.values().forEach(examRoomInfoList -> examRoomInfoList.forEach(examRoomInfo -> examRoomInfo.setList(new ArrayList<>()))));

        List<ExamPlaceInfo> examPlaceInfoList = ExamPlaceInfo.processPersonByGroup(personInfoListCopy, IndexFxml.placeSubjectRoomInfoMap, random);
        allExamInfo.setList(examPlaceInfoList);
        allExamInfo.processPicSrc(IndexFxml.picPath);

        AllExamInfo res = new AllExamInfo();
        res.setList(allExamInfo.getList());
        return res;
    }

    @RequestMapping("/getSubjectInfoMaxCountList")
    public List<ExamRoomInfo> getSubjectInfoMaxCountList() {
        List<ExamRoomInfo> res = new ArrayList<>();
        for (Map<String, List<ExamRoomInfo>> map : IndexFxml.placeSubjectRoomInfoMap.values()) {
            for (List<ExamRoomInfo> list : map.values()) {
                res.addAll(list);
            }
        }
        res.sort(Comparator.comparing(ExamRoomInfo::getId));
        return res;
    }

    @RequestMapping("/updateMaxCount")
    public Boolean updateMaxCount(@RequestBody List<ExamRoomInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }

        for (ExamRoomInfo examRoomInfo : list) {
            String examPlaceId = examRoomInfo.getExamPlaceId();
            String subjectType = examRoomInfo.getSubjectType();
            Map<String, List<ExamRoomInfo>> map = IndexFxml.placeSubjectRoomInfoMap.get(examPlaceId);
            if (MapUtils.isEmpty(map)) {
                if (IndexFxml.placeSubjectRoomInfoMap.size() != 1) {
                    continue;
                }
                map = IndexFxml.placeSubjectRoomInfoMap.get(null);
                if (MapUtils.isEmpty(map)) {
                    continue;
                }
            }
            List<ExamRoomInfo> examRoomInfos = map.get(subjectType);
            if (CollectionUtils.isEmpty(examRoomInfos)) {
                continue;
            }
            for (ExamRoomInfo roomInfo : examRoomInfos) {
                if (roomInfo.getId().equals(examRoomInfo.getId())) {
                    roomInfo.setTime(examRoomInfo.getTime());
                    roomInfo.setRoomCount(examRoomInfo.getRoomCount());
                    roomInfo.setMaxCount(examRoomInfo.getMaxCount());
                    roomInfo.setRoomName(examRoomInfo.getRoomName());
                    break;
                }
            }
        }

        return true;
    }

    @RequestMapping("/exportExcel")
    public Boolean exportExcel(String exportFileName) {
        List<PersonInfo> data = new ArrayList<>();
        List<ExamPlaceInfo> list = allExamInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        for (ExamPlaceInfo examPlaceInfo : list) {
            for (ExamRoomInfo examRoomInfo : examPlaceInfo.getList()) {
                if (CollectionUtils.isEmpty(examRoomInfo.getRoomList())) {
                    continue;
                }
                for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                    data.addAll(roomInfo.getPersons());
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

        Platform.runLater(() -> {
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
