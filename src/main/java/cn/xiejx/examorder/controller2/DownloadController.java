package cn.xiejx.examorder.controller2;

import cn.xiejx.examorder.constants.Constants;
import org.springframework.http.*;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * There is description
 * @author sleepybear
 * @date 2023/10/07 15:24
 */
@RestController
@RequestMapping(value = Constants.PREFIX)
public class DownloadController {
    //    @GetMapping("/download/downloadFile")
//    public ResponseEntity<byte[]> downloadFile(String exportKey, HttpServletResponse response) {
//        response.setCharacterEncoding("UTF-8");
//
//
//
//        try {
//            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//                    .filename(URLEncoder.encode(downloadInfoDto.getFilename(), StandardCharsets.UTF_8))
//                    .build();
//
//            byte[] content = Files.readAllBytes(Paths.get(fullFilePath));
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .contentLength(content.length)
//                    .body(content);
//        } catch (IOException e) {
//            throw new FrontException(e.getMessage());
//        }
//    }
//
//    @GetMapping("/download/downloadResourceFile")
//    public ResponseEntity<byte[]> downloadFile(String filename) {
//        try {
//            // 读取静态资源下的文件
//            File file = ResourceUtils.getFile("classpath:static/" + filename);
//            if (!file.exists()) {
//                return generateErrorResponse("文件不存在");
//            }
//            byte[] content = StreamUtils.copyToByteArray(new FileInputStream(file));
//            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
//                    .filename(URLEncoder.encode(filename, StandardCharsets.UTF_8))
//                    .build();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .contentLength(content.length)
//                    .body(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private ResponseEntity<byte[]> generateErrorResponse(String errorMessage) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(errorMessage.getBytes(StandardCharsets.UTF_8));
//    }
}
