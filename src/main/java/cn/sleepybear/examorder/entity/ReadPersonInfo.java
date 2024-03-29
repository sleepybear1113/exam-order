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
    private Integer personCount;
    private List<String> validList;

    /**
     * 复制 ReadPersonInfo 对象，可选择是否复制 personInfoList
     *
     * @param copyPersonInfoList 是否复制 personInfoList，true 表示复制，false 表示不复制
     * @return 返回复制后的 ReadPersonInfo 对象
     */
    public ReadPersonInfo copy(boolean copyPersonInfoList) {
        ReadPersonInfo readPersonInfo = new ReadPersonInfo();
        readPersonInfo.setKey(this.key);
        readPersonInfo.setPersonCount(this.personCount);
        if (copyPersonInfoList) {
            readPersonInfo.setPersonInfoList(this.personInfoList);
        }
        readPersonInfo.setValidList(this.validList);
        return readPersonInfo;
    }
}
