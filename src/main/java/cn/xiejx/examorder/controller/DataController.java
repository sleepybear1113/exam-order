package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.ReadPersonInfo;
import cn.xiejx.examorder.entity.ReadRoomInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/21 15:21
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
@Slf4j
public class DataController {

    @RequestMapping("/data/getReadPersonInfo")
    public ReadPersonInfo getReadPersonInfo(String personInfoKey) {
        ReadPersonInfo readPersonInfo = Constants.READ_PERSON_INFO_CACHER.get(personInfoKey);
        if (readPersonInfo == null) {
            return null;
        }

        return readPersonInfo.copy(false);
    }

    @RequestMapping("/data/getReadRoomInfo")
    public ReadRoomInfo getRoomInfo(String placeSubjectRoomInfoKey) {
        ReadRoomInfo readRoomInfo = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(placeSubjectRoomInfoKey);
        if (readRoomInfo == null) {
            return null;
        }

        return readRoomInfo.copy(false);
    }
}
