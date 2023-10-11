package cn.xiejx.examorder.controller;

import cn.xiejx.examorder.advice.ResultCode;
import cn.xiejx.examorder.config.AppProperties;
import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.AllExamInfo;
import cn.xiejx.examorder.entity.ExamPlaceInfo;
import cn.xiejx.examorder.entity.ExamRoomInfo;
import cn.xiejx.examorder.entity.PersonInfo;
import cn.xiejx.examorder.exception.FrontException;
import cn.xiejx.examorder.utils.Util;
import com.alibaba.excel.EasyExcel;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * There is description
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
