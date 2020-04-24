package io.wangxin.result;


/**
 * common Exception with  @see IFailCode
 *
 * @author Xin Wang
 */
public class SmartException extends Exception {
    private int code;
    private String desc;

    public SmartException(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public SmartException(IFailCode failCodeEnum) {
        this.code = failCodeEnum.getValue();
        this.desc = failCodeEnum.getDesc();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
