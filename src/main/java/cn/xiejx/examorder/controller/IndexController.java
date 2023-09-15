package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.config.AppProperties;
import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.*;
import cn.xiejx.examorder.fxml.IndexFxml;
import cn.xiejx.examorder.utils.SpringContextUtil;
import cn.xiejx.examorder.utils.Util;
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

    @RequestMapping("/processPersonByGroup")
    public AllExamInfo processPersonByGroup(Long random, String personInfoKey, String placeSubjectRoomInfoKey) {
        if (StringUtils.isBlank(personInfoKey) && StringUtils.isBlank(placeSubjectRoomInfoKey)) {
            personInfoKey = IndexFxml.personInfoKey;
            placeSubjectRoomInfoKey = IndexFxml.placeSubjectRoomInfoKey;
        }

        AllExamInfo allExamInfo = new AllExamInfo();
        String key = Util.getRandomStr(8);
        allExamInfo.setId(key);
        ReadPersonInfo readPersonInfo = Constants.READ_PERSON_INFO_CACHER.get(personInfoKey);
        Map<String, Map<String, List<ExamRoomInfo>>> mapMap = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(placeSubjectRoomInfoKey);
        if (readPersonInfo == null || mapMap == null) {
            return allExamInfo;
        }

        if (CollectionUtils.isEmpty(readPersonInfo.getPersonInfoList())) {
            return allExamInfo;
        }
        List<PersonInfo> personInfoListCopy = new ArrayList<>();
        for (PersonInfo personInfo : readPersonInfo.getPersonInfoList()) {
            personInfoListCopy.add(personInfo.copy());
        }

        mapMap.values().forEach(map -> map.values().forEach(examRoomInfoList -> examRoomInfoList.forEach(examRoomInfo -> examRoomInfo.setList(new ArrayList<>()))));

        List<ExamPlaceInfo> examPlaceInfoList = ExamPlaceInfo.processPersonByGroup(personInfoListCopy, mapMap, random);
        allExamInfo.setList(examPlaceInfoList);
        allExamInfo.processPicSrc(IndexFxml.picPath);

        AllExamInfo res = new AllExamInfo();
        res.setId(allExamInfo.getId());
        res.setList(allExamInfo.getList());
//        res.setList(res.getList().subList(0,2));
        res.setRooms(Room.build(res.getList()));
        res.setPersons(Room.getSortedPersons(res.getRooms()));

        Constants.ALL_EXAM_INFO_CACHER.set(key, allExamInfo);
        return res;
    }

    @RequestMapping("/getSubjectInfoMaxCountList")
    public List<ExamRoomInfo> getSubjectInfoMaxCountList() {
        List<ExamRoomInfo> res = new ArrayList<>();
        for (Map<String, List<ExamRoomInfo>> map : Constants.EXAM_ROOM_INFO_MAP_CACHER.get(IndexFxml.placeSubjectRoomInfoKey).values()) {
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
            Map<String, Map<String, List<ExamRoomInfo>>> mapMap = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(IndexFxml.placeSubjectRoomInfoKey);
            Map<String, List<ExamRoomInfo>> map = mapMap.get(examPlaceId);
            if (MapUtils.isEmpty(map)) {
                if (mapMap.size() != 1) {
                    continue;
                }
                map = mapMap.get(null);
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
    public Boolean exportExcel(String exportFileName, String key) {
        AllExamInfo allExamInfo = Constants.ALL_EXAM_INFO_CACHER.get(key);
        if (allExamInfo == null) {
            return false;
        }
        List<ExamPlaceInfo> list = allExamInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        List<PersonInfo> data = new ArrayList<>();
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

    @RequestMapping("/getVersion")
    public String getVersion() {
        return SpringContextUtil.getBean(AppProperties.class).getVersion();
    }
}
