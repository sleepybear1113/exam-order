package cn.sleepybear.examorder.controller;

import cn.sleepybear.examorder.config.AppProperties;
import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.*;
import cn.sleepybear.examorder.utils.SpringContextUtil;
import cn.sleepybear.examorder.utils.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

        AllExamInfo res = new AllExamInfo();
        res.setId(allExamInfo.getId());
        res.setList(allExamInfo.getList());
        res.setRooms(Room.build(res.getList()));
        res.setPersons(Room.getSortedPersons(res.getRooms()));

        Constants.ALL_EXAM_INFO_CACHER.set(key, allExamInfo);
        return res;
    }

    @RequestMapping("/getVersion")
    public String getVersion() {
        return SpringContextUtil.getBean(AppProperties.class).getVersion();
    }
}
