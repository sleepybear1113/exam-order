package cn.sleepybear.examorder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/03/14 23:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileBytes implements Serializable {
    @Serial
    private static final long serialVersionUID = -1235064909035072589L;

    private String filename;
    private byte[] bytes;
    private MediaType contentType;

    public FileBytes(String filename, byte[] bytes) {
        this.filename = filename;
        this.bytes = bytes;
        this.contentType = MediaType.APPLICATION_OCTET_STREAM;
    }

    public MediaType getContentType() {
        return contentType == null ? MediaType.APPLICATION_OCTET_STREAM : contentType;
    }
}
