package com.itheima.recorddid.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsultSendVO {
    @NotNull(message = "唱片id不能为空")
    private Long recordId;

    @NotBlank(message = "咨询问题不能为空")
    private String question;
}