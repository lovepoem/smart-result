package io.wangxin.result.utils;

import io.wangxin.result.IFailCode;
import io.wangxin.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.wangxin.result.IFailCode.SYSTEM_EXCEPTION_CODE;
import static io.wangxin.result.IFailCode.SYSTEM_EXCEPTION_MSG;

/**
 * Encapsulation of Result
 *
 * @author Xin Wang
 */
public class ResultUtils {
    private static Logger logger = LoggerFactory.getLogger(ResultUtils.class);
    /**
     * 0: success code
     */
    public static final int SUCCESS_CODE = 0;

    /***
     * wrap success result
     * @param data return data
     * @param <T>
     * @return
     */
    public static <T> Result<T> wrapSuccess(T data) {
        Result<T> result = new Result<T>();
        result.setCode(SUCCESS_CODE);
        result.setMessage("");
        result.setData(data);
        return result;
    }

    /***
     * wrap failure result ，return data is null
     * @param
     * @param <T> return data
     * @return
     */
    public static <T> Result wrapSuccess() {
        return wrapSuccess(null);
    }

    /***
     * wrap failure result
     * @param code error code
     * @param message error message
     * @return
     */
    public static Result wrapFailure(int code, String message) {
        Result result = new Result(code, message);
        return result;
    }

    /***
     * wrap failure result
     * @param failCodeDesc
     * @return
     */
    public static Result wrapFailure(IFailCode failCodeDesc) {
        return wrapFailure(failCodeDesc.getValue(), failCodeDesc.getDesc());
    }

    /***
     * wrap failure result, Use when the error message has variables
     * @param failCode
     * @param msgValues dynamic parameters of error message
     * @return
     */
    public static Result wrapFailure(IFailCode failCode, String... msgValues) {
        String msg = failCode.getDesc();
        if (msgValues != null && msgValues.length > 0) {
            msg = String.format(failCode.getDesc(), msgValues);
        }
        return wrapFailure(failCode.getValue(), msg);
    }

    /**
     * @return Print error and return failure, system level
     * @see Result ，Unified printing exception
     */
    public static Result wrapException(Exception e) {
        logger.error("Interface throws an exception:", e);
        return wrapFailure(SYSTEM_EXCEPTION_CODE, SYSTEM_EXCEPTION_MSG);
    }

    /**
     * @return Print error and return failure, system level
     * @see Result . You can use this if you don't
     * need to print exceptions uniformly
     */
    public static Result wrapException() {
        return wrapFailure(SYSTEM_EXCEPTION_CODE, SYSTEM_EXCEPTION_MSG);
    }
}
