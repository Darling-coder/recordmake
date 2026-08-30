

package com.itheima.recorddid.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateVO {
    @NotNull(message = "唱片id不能为空")
    private Long recordId;
    private Integer buyNum;

    @NotBlank(message = "收货地址不能为空")
    private String address;
}