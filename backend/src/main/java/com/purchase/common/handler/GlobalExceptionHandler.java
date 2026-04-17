package com.purchase.common.handler;

import com.purchase.common.Result;
import com.purchase.common.exception.BusinessException;
import com.purchase.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常, msg={}", e.getMessage(), e);
        return Result.error(ResultCodeEnum.DEFAULT_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(","));
        log.error("参数校验异常, msg={}", message, e);
        return Result.error(ResultCodeEnum.PARAM_INVALID, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("未知异常, msg={}", e.getMessage(), e);
        return Result.error(ResultCodeEnum.UNKNOWN_EXCEPTION, e.getMessage());
    }
}
