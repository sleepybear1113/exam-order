package cn.sleepybear.examorder.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/09/12 16:23
 */
@Data
public class ReadPersonInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -690972143031845640L;

    private String key;
    private List<PersonInfo> personInfoList;
    private List<String> validList;

    public ReadPersonInfo copy(boolean copyPersonInfoList) {
        ReadPersonInfo readPersonInfo = new ReadPersonInfo();
        readPersonInfo.setKey(this.key);
        if (copyPersonInfoList) {
            readPersonInfo.setPersonInfoList(this.personInfoList);
        }
        readPersonInfo.setValidList(this.validList);
        return readPersonInfo;
    }
}
