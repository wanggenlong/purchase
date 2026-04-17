package com.purchase.common;

import com.purchase.enums.ResultCodeEnum;
import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getDesc());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(resultCodeEnum.getDesc());
        return result;
    }

    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum, String msg) {
        Result<T> result = new Result<>();
        result.setCode(resultCodeEnum.getCode());
        result.setMsg(msg);
        return result;
    }
}
