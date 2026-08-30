package com.itheima.recorddid.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("consult")
public class Consult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long buyerId;
    private Long sellerId;
    // 买家提问（替换原来的content）
    private String question;
    // 卖家回复内容
    private String reply;
    // 0=未回复 1=已回复
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime replyTime;
}