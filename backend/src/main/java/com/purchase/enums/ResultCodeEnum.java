package com.purchase.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 失败的错误吗从-1开始递减，并不得超过-99
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {
    SUCCESS(0, "成功"),
    DEFAULT_EXCEPTION(-1, "默认异常"),
    PARAM_INVALID(-2, "参数错误"),
    USER_NOT_FOUND(-3, "用户不存在"),
    PASSWORD_ERROR(-4, "密码错误"),
    USER_DISABLED(-5, "账号已被禁用"),
    UNKNOWN_EXCEPTION(-99, "未知异常");

    final int code;
    final String desc;
}
