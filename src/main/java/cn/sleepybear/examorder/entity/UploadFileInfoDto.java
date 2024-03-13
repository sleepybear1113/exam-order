package cn.sleepybear.examorder.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/14 16:19
 */
@Data
public class UploadFileInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1719913206743663880L;

    private String id;
    private String fileStreamDtoId;
    private String filename;
}
