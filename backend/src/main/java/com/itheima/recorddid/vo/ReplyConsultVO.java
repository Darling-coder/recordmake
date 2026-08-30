package com.itheima.recorddid.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplyConsultVO {
    @NotNull(message = "咨询记录id不能为空")
    private Long consultId;

    @NotBlank(message = "回复内容不能为空")
    private String replyContent;
}