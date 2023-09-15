package cn.xiejx.examorder.logic;

import cn.xiejx.examorder.config.AppProperties;
import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.FileStreamDto;
import cn.xiejx.examorder.entity.UploadFileInfoDto;
import cn.xiejx.examorder.exception.FrontException;
import cn.xiejx.examorder.utils.SpringContextUtil;
import cn.xiejx.examorder.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/10 12:11
 */
@Component
@Slf4j
public class UploadLogic {
    public static final int DEFAULT_EXPIRE_MINUTES_TIME = 60;

    public UploadFileInfoDto uploadFile(MultipartFile file, String fileType, Boolean deleteAfterUpload, Integer expireTimeMinutes) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || StringUtils.isBlank(originalFilename)) {
            originalFilename = "null";
        }
        String dot = ".";
        if (StringUtils.isBlank(fileType) && StringUtils.contains(originalFilename, dot)) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf(dot));
        }
        if (!".xls".equals(fileType) && !".xlsx".equals(fileType)) {
            throw new FrontException("需要为Excel文件（xlsx格式）！");
        }

        if (expireTimeMinutes == null || expireTimeMinutes <= 0) {
            expireTimeMinutes = DEFAULT_EXPIRE_MINUTES_TIME;
        }
        long expireTime = expireTimeMinutes * 60 * 1000L;

        FileStreamDto fileStreamDto = getInputStream(file, fileType, deleteAfterUpload);
        Constants.FILE_STREAM_DTO_CACHER.put(fileStreamDto.getId(), fileStreamDto);

        UploadFileInfoDto uploadFileInfoDto = new UploadFileInfoDto();
        uploadFileInfoDto.setFilename(fileStreamDto.getOriginalFilename());
        uploadFileInfoDto.setFileStreamDtoId(fileStreamDto.getId());
        return uploadFileInfoDto;
    }

    public FileStreamDto getInputStream(MultipartFile file, String fileType, Boolean deleteAfterUpload) {
        FileStreamDto fileStreamDto = new FileStreamDto();
        if (file == null) {
            return fileStreamDto;
        }
        String originalFilename = file.getOriginalFilename();
        fileStreamDto.setOriginalFilename(originalFilename);
        fileStreamDto.setTempFilename(generateTempFilename(originalFilename, fileType));
        fileStreamDto.setFileType(fileType);
        fileStreamDto.setCreateTime(System.currentTimeMillis());

        if (deleteAfterUpload) {
            // 上传后立即删除，那么不保留本地文件，直接流读取
            fileStreamDto.setByteArrayInputStream(file);
        } else {
            // 上传不删除文件的
            // 开始生成临时文件名
            String filename = fileStreamDto.getTempFilename();
            // 读取写入文件
            Util.ensureParentDir(filename);
            File localFile = new File(filename);
            try {
                FileOutputStream out = new FileOutputStream(localFile);
                file.getInputStream().transferTo(out);
                out.close();
                fileStreamDto.setByteArrayInputStream(localFile);
            } catch (IOException e) {
                throw new FrontException(e.getMessage());
            }
        }
        fileStreamDto.setId(Util.bytesToMd5(fileStreamDto.getBytes()));
        log.info("文件接收成功，id = {}，文件名 = {}，大小 = {}，是否保存 = {}", fileStreamDto.getId(), fileStreamDto.getOriginalFilename(), Util.getFileSize(file.getSize()), Boolean.FALSE.equals(deleteAfterUpload));

        return fileStreamDto;
    }

    public static String generateTempFilename(String originalFilename, String fileType) {
        if (StringUtils.isBlank(originalFilename)) {
            originalFilename = "";
        }
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String filenamePrefix;
        String suffix = StringUtils.isBlank(fileType) ? "" : fileType;
        if (lastIndexOf < 0 || StringUtils.isNotBlank(suffix)) {
            // 文件没有 .xxx 后缀，或者已有给出后缀的情况下
            filenamePrefix = originalFilename;
        } else {
            filenamePrefix = originalFilename.substring(0, lastIndexOf);
            suffix = originalFilename.substring(lastIndexOf);
        }

        return SpringContextUtil.getBean(AppProperties.class).getTmpDir() + filenamePrefix + "-" + Util.getTime() + suffix;
    }
}
