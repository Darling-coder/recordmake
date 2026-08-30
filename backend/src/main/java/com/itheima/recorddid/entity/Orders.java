package com.itheima.recorddid.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {
    private Long id;
    private Long buyerId;
    private Long sellerId;
    private Long recordId;
    private Integer buyNum;
    private BigDecimal totalPrice;
    private String address;
    private Integer orderStatus;
    // 下单快照-唱片名称
    private String recordName;
    // 下单快照-唱片单价
    private BigDecimal price;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}