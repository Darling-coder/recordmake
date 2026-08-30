package com.itheima.recorddid.exception;

import lombok.Data;

/**
 * 自定义业务异常：用于手动抛出业务错误（库存不足、无权限、商品不存在等）
 */
@Data
public class BusinessException extends RuntimeException {
    // 自定义错误码
    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}