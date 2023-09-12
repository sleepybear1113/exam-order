package cn.xiejx.examorder.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/12 16:48
 */
@Data
@Slf4j
public class FileStreamDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 3960579933686268396L;

    private String id;
    private String tempFilename;
    private String originalFilename;
    private String fileType;
    private byte[] bytes;
    private Boolean localFile;
    private Long createTime;
    private Long expireTime;
    private String errorMessage;

    public FileStreamDto() {
    }

    public boolean setByteArrayInputStream(InputStream inputStream) {
        if (inputStream == null) {
            log.info("input stream is null!");
            return false;
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buffer = new byte[40960];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                b.write(buffer, 0, len);
            }
            b.flush();
            this.bytes = b.toByteArray();
            this.createTime = System.currentTimeMillis();
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            this.errorMessage = e.getMessage();
            return false;
        }
    }

    public boolean setByteArrayInputStream(MultipartFile multipartFile) {
        try {
            setByteArrayInputStream(multipartFile.getInputStream());
            this.originalFilename = multipartFile.getOriginalFilename();
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            this.errorMessage = e.getMessage();
            return false;
        }
    }

    public boolean setByteArrayInputStream(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            setByteArrayInputStream(fileInputStream);
            this.localFile = true;
            this.originalFilename = file.getName();
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            this.errorMessage = e.getMessage();
            return false;
        }
    }

    public ByteArrayInputStream getByteArrayInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }
}