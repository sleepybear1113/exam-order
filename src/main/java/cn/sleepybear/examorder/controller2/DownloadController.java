package cn.sleepybear.examorder.controller2;

import cn.sleepybear.examorder.constants.Constants;
import cn.sleepybear.examorder.entity.FileBytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/10/07 15:24
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
@Slf4j
public class DownloadController {

    /**
     * 下载文件
     *
     * @param key 文件缓存的 key 值
     * @return 包含文件字节内容的 ResponseEntity
     */
    @GetMapping("/download/downloadFile")
    public ResponseEntity<byte[]> downloadFile(String key) {
        // 判断 key 是否为空
        if (StringUtils.isEmpty(key)) {
            // 如果 key 为空，返回错误信息
            return generateErrorResponse("key不能为空");
        }

        // 从缓存中获取文件字节信息
        FileBytes fileBytes = Constants.FILE_BYTES_EXPORT_CACHER.get(key);
        // 判断文件字节信息是否为空
        if (fileBytes == null) {
            // 如果文件字节信息为空，返回错误信息
            return generateErrorResponse("文件已过期或者不存在");
        }

        // 获取文件字节
        byte[] bytes = fileBytes.getBytes();

        // 设置文件下载时的内容处置，包括文件名和编码方式
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(URLEncoder.encode(fileBytes.getFilename(), StandardCharsets.UTF_8))
                .build();

        // 构建返回的文件响应
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(fileBytes.getContentType())
                .contentLength(bytes.length)
                .body(bytes);
    }


    /**
     * 生成错误响应的实体对象
     *
     * @param errorMessage 错误信息
     * @return ResponseEntity 实体对象，包含错误信息的字节数组
     */
    private ResponseEntity<byte[]> generateErrorResponse(String errorMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(errorMessage.getBytes(StandardCharsets.UTF_8));
    }
}
