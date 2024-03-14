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

    /**
     * 根据分组处理考生信息
     *
     * @param random                  随机数，如果为空就不随机
     * @param personInfoKey           考生信息 Excel 文件读取缓存的 key
     * @param placeSubjectRoomInfoKey 考场信息 Excel 文件读取缓存的 key
     * @return 返回处理后的 AllExamInfo 对象
     */
    @RequestMapping("/processPersonByGroup")
    public AllExamInfo processPersonByGroup(Long random, String personInfoKey, String placeSubjectRoomInfoKey) {
        // 创建一个 AllExamInfo 对象
        AllExamInfo allExamInfo = new AllExamInfo();
        // 生成一个随机的 key
        String key = Util.getRandomStr(8);
        // 设置 AllExamInfo 对象的 id 为生成的 key
        allExamInfo.setId(key);

        // 从缓存中获取 ReadPersonInfo 对象
        ReadPersonInfo readPersonInfo = Constants.READ_PERSON_INFO_CACHER.get(personInfoKey);
        // 从缓存中获取 ReadRoomInfo 对象
        ReadRoomInfo readRoomInfo = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(placeSubjectRoomInfoKey);

        // 如果 ReadPersonInfo 或 ReadRoomInfo 对象为空，则直接返回空的 AllExamInfo 对象
        if (readPersonInfo == null || readRoomInfo == null) {
            return allExamInfo;
        }

        // 获取 ReadRoomInfo 对象的 mapMap 属性
        Map<String, Map<String, List<ExamRoomInfo>>> mapMap = readRoomInfo.getMapMap();

        // 如果 ReadPersonInfo 对象的 personInfoList 属性为空，则直接返回空的 AllExamInfo 对象
        if (CollectionUtils.isEmpty(readPersonInfo.getPersonInfoList())) {
            return allExamInfo;
        }

        // 复制 ReadPersonInfo 对象的 personInfoList 属性
        List<PersonInfo> personInfoListCopy = new ArrayList<>();
        for (PersonInfo personInfo : readPersonInfo.getPersonInfoList()) {
            personInfoListCopy.add(personInfo.copy());
        }

        // 清空所有考试房间的列表
        mapMap.values().forEach(map -> map.values().forEach(examRoomInfoList -> examRoomInfoList.forEach(examRoomInfo -> examRoomInfo.setList(new ArrayList<>()))));

        // 处理分组后的考试地点信息
        List<ExamPlaceInfo> examPlaceInfoList = ExamPlaceInfo.processPersonByGroup(personInfoListCopy, mapMap, random);
        // 设置 AllExamInfo 对象的 list 属性为处理后的考试地点信息
        allExamInfo.setList(examPlaceInfoList);

        // 创建一个新的 AllExamInfo 对象 res
        AllExamInfo res = new AllExamInfo();
        // 设置 res 对象的 id 属性为 allExamInfo 对象的 id
        res.setId(allExamInfo.getId());
        // 设置 res 对象的 list 属性为 allExamInfo 对象的 list
        res.setList(allExamInfo.getList());
        // 设置 res 对象的 rooms 属性为根据考试地点信息构建的考试房间信息
        res.setRooms(Room.build(res.getList()));
        // 设置 res 对象的 persons 属性为根据考试房间信息排序后的考生信息
        res.setPersons(Room.getSortedPersons(res.getRooms()));

        // 将 allExamInfo 对象存入缓存
        Constants.ALL_EXAM_INFO_CACHER.set(key, allExamInfo);
        // 返回 res 对象
        return res;
    }


    @RequestMapping("/getVersion")
    public String getVersion() {
        return SpringContextUtil.getBean(AppProperties.class).getVersion();
    }
}
