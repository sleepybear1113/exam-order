package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.config.AppProperties;
import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.*;
import cn.xiejx.examorder.fxml.IndexFxml;
import cn.xiejx.examorder.utils.SpringContextUtil;
import cn.xiejx.examorder.utils.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/06 09:20
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
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
        ReadRoomInfo readRoomInfo = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(placeSubjectRoomInfoKey);
        if (readPersonInfo == null || readRoomInfo == null) {
            return allExamInfo;
        }

        Map<String, Map<String, List<ExamRoomInfo>>> mapMap = readRoomInfo.getMapMap();
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
        res.setRooms(Room.build(res.getList()));
        res.setPersons(Room.getSortedPersons(res.getRooms()));

        Constants.ALL_EXAM_INFO_CACHER.set(key, allExamInfo);
        return res;
    }

    @RequestMapping("/getSubjectInfoMaxCountList")
    public List<ExamRoomInfo> getSubjectInfoMaxCountList() {
        List<ExamRoomInfo> res = new ArrayList<>();
        ReadRoomInfo readRoomInfo = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(IndexFxml.placeSubjectRoomInfoKey);
        for (Map<String, List<ExamRoomInfo>> map : readRoomInfo.getMapMap().values()) {
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
            ReadRoomInfo readRoomInfo = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(IndexFxml.placeSubjectRoomInfoKey);
            Map<String, Map<String, List<ExamRoomInfo>>> mapMap = readRoomInfo.getMapMap();
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

    @RequestMapping("/getVersion")
    public String getVersion() {
        return SpringContextUtil.getBean(AppProperties.class).getVersion();
    }
}
