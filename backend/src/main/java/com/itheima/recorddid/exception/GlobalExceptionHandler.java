package com.itheima.recorddid.exception;

import com.itheima.recorddid.common.Result;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常统一拦截，所有接口报错统一返回标准Result格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 捕获我们手动抛出的业务异常（最常用）
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 2. 捕获 @NotBlank / @NotNull 等参数校验失败异常
    @ExceptionHandler(BindException.class)
    public Result<?> handleValidException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(400, "参数校验失败：" + errorMsg);
    }

    // 3. 兜底：捕获所有未分类的未知系统异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleAllException(Exception e) {
        // 打印异常堆栈，方便后端排查bug
        e.printStackTrace();
        return Result.fail(500, "服务器内部异常，请稍后重试或联系管理员");
    }
}