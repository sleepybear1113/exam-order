package cn.sleepybear.examorder.controller;

import cn.sleepybear.examorder.advice.ResultCode;
import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.*;
import cn.sleepybear.examorder.exception.FrontException;
import cn.sleepybear.examorder.utils.Util;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
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
@Slf4j
public class ExportController {

    /**
     * 导出 Excel 表格
     *
     * @param key 缓存 key
     * @return 返回文件的 key
     * @throws FrontException 数据已过期或者不存在等报错
     */
    @RequestMapping("/exportExcel")
    public ResultCode<String> exportExcel(String key) {
        // 从缓存中获取 AllExamInfo 对象
        AllExamInfo allExamInfo = Constants.ALL_EXAM_INFO_CACHER.get(key);
        if (allExamInfo == null) {
            throw new FrontException("数据已过期或者不存在");
        }
        // 获取 AllExamInfo 中的考试地点列表
        List<ExamPlaceInfo> list = allExamInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            throw new FrontException("数据已过期或者不存在");
        }
        // 初始化存储人员信息的列表
        List<PersonInfo> data = new ArrayList<>();
        // 遍历考试地点列表
        for (ExamPlaceInfo examPlaceInfo : list) {
            // 遍历考试地点中的考场列表
            for (ExamRoomInfo examRoomInfo : examPlaceInfo.getList()) {
                // 如果考场列表为空，则跳过当前循环
                if (CollectionUtils.isEmpty(examRoomInfo.getRoomList())) {
                    continue;
                }
                // 遍历考场列表
                for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                    // 将考场中的人员信息添加到数据列表中
                    data.addAll(roomInfo.getPersons());
                }
            }
        }
        // 如果数据列表为空，则抛出异常
        if (CollectionUtils.isEmpty(data)) {
            throw new FrontException("数据已过期或者不存在");
        }

        // 构造导出文件的文件名
        String filename = "导出安排表-%s.xlsx".formatted(System.currentTimeMillis());
        // 创建字节输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 使用 EasyExcel 将数据写入到字节输出流中
        EasyExcel.write(outputStream, PersonInfo.class)
                .sheet("sheet1")
                .doWrite(data);

        // 将字节输出流转换为字节数组
        byte[] byteArray = outputStream.toByteArray();
        // 生成一个随机的文件 key
        String excelFileKey = Util.getRandomStr(8);
        // 将字节数组和文件名封装为 FileBytes 对象，并存储到缓存中
        Constants.FILE_BYTES_EXPORT_CACHER.set(excelFileKey, new FileBytes(filename, byteArray), 3600 * 1000L);

        // 记录导出成功的日志
        log.info("导出成功, key = {}, filename = {}, size = {}", excelFileKey, filename, Util.getFileSize((long) byteArray.length));
        // 返回文件的 key
        return ResultCode.buildResult(excelFileKey);
    }

}
