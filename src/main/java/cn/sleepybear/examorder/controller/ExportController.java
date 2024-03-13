package cn.sleepybear.examorder.controller;

import cn.sleepybear.examorder.advice.ResultCode;
import cn.sleepybear.examorder.config.AppProperties;
import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.AllExamInfo;
import cn.sleepybear.examorder.entity.ExamPlaceInfo;
import cn.sleepybear.examorder.entity.ExamRoomInfo;
import cn.sleepybear.examorder.entity.PersonInfo;
import cn.sleepybear.examorder.exception.FrontException;
import cn.sleepybear.examorder.utils.Util;
import com.alibaba.excel.EasyExcel;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/10/11 10:12
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
public class ExportController {
    @Resource
    private AppProperties appProperties;

    @RequestMapping("/exportExcel")
    public ResultCode<String> exportExcel(String key) {
        AllExamInfo allExamInfo = Constants.ALL_EXAM_INFO_CACHER.get(key);
        if (allExamInfo == null) {
            throw new FrontException("数据已过期或者不存在");
        }
        List<ExamPlaceInfo> list = allExamInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            throw new FrontException("数据已过期或者不存在");
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
            throw new FrontException("数据已过期或者不存在");
        }

        String path = appProperties.getExportTmpDir() + "导出安排表-%s.xlsx".formatted(System.currentTimeMillis());
        Util.ensureParentDir(path);
        EasyExcel.write(path, PersonInfo.class)
                .sheet("sheet1")
                .doWrite(() -> data);

        String key1 = Util.getRandomStr(8);
        Constants.FILE_EXPORT_CACHER.set(key1, path, 3600 * 1000L);

        return ResultCode.buildResult(key1);
    }
}
