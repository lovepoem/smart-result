package io.wangxin.result;


import java.io.Serializable;

/**
 * Data wrapper class returned by Api
 *
 * @param <T> return data
 * @author Xin Wang
 */
public class Result<T> implements Serializable, Cloneable {
    private static final long serialVersionUID = -4233732342342357310L;
    /**
     * return value
     **/
    private T data;
    /**
     * error code
     */
    private int code;
    /**
     * error message
     */
    private String message;
    private static final int SUCCESS_CODE = 0;

    public Result() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;

    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
