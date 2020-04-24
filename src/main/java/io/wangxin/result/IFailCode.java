package io.wangxin.result;

/**
 * @author Xin Wang
 */
public interface IFailCode {
    Integer SYSTEM_EXCEPTION_CODE = 500;
    String SYSTEM_EXCEPTION_MSG = "System Exception";

    Integer OBJECT_NOT_FOUND = 404;
    String OBJECT_NOT_FOUND_MSG = "Object Not Found";

    /**
     * @return the value
     */
    int getValue();

    /**
     * @return the desc
     */
    String getDesc();
}
