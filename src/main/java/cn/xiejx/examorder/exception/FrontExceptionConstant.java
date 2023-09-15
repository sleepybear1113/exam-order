package cn.xiejx.examorder.exception;

import lombok.Getter;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/18 20:06
 */
public class FrontExceptionConstant {
    @Getter
    public enum TypeEnum {
        /**
         * 普通错误
         */
        NORMAL(0),

        /**
         * 系统错误
         */
        SYSTEM(1),
        ;
        private final Integer type;

        TypeEnum(Integer type) {
            this.type = type;
        }

    }
}
