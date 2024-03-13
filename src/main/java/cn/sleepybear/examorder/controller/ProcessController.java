package cn.sleepybear.examorder.controller;

import cn.sleepybear.examorder.advice.ResultCode;
import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.FileStreamDto;
import cn.sleepybear.examorder.exception.FrontException;
import cn.sleepybear.examorder.logic.ProcessLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/16 09:31
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
@Slf4j
public class ProcessController {

    @RequestMapping("/process/process")
    public ResultCode<String> process(String fileStreamDtoId, Integer type) {
        FileStreamDto fileStreamDto = Constants.FILE_STREAM_DTO_CACHER.get(fileStreamDtoId);
        if (fileStreamDtoId == null) {
            throw new FrontException("文件流不存在！");
        }

        if (type == null) {
            throw new FrontException("参数错误！");
        }

        String key = null;
        if (type == 1) {
            key = ProcessLogic.readPersonExcel(fileStreamDto);
        } else if (type == 2) {
            key = ProcessLogic.readRoomExcel(fileStreamDto);
        }

        return ResultCode.buildResult(key);
    }
}
