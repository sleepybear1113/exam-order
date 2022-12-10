package cn.xiejx.examorder.entity;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/09 15:09
 */
@Data
public class AllExamInfo {
    private List<ExamPlaceInfo> list;

    public void processPicSrc(String picSrcPrefix) {
        if (StringUtils.isBlank(picSrcPrefix) || CollectionUtils.isEmpty(list)) {
            return;
        }

        File dir = new File(picSrcPrefix);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        Map<String,File> fileNameMap = new HashMap<>();
        for (File file : files) {
            fileNameMap.put(file.getName().split("\\.")[0], file);
        }

        for (ExamPlaceInfo examPlaceInfo : list) {
            List<ExamRoomInfo> examRoomInfos = examPlaceInfo.getList();
            for (ExamRoomInfo examRoomInfo : examRoomInfos) {
                for (List<PersonInfo> personInfoList : examRoomInfo.getList()) {
                    for (PersonInfo personInfo : personInfoList) {
                        personInfo.buildPicSrc(fileNameMap);
                    }
                }
            }
        }
    }
}
