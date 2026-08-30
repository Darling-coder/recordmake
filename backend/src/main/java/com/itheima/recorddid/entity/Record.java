package com.itheima.recorddid.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("record")
public class Record {
    // 唱片主键id，数据库自增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 唱片名称
    @NotBlank(message = "唱片名称不能为空")
    private String recordName;

    // 歌手/艺术家
    @NotBlank(message = "歌手名称不能为空")
    private String artist;

    // 售价
    private BigDecimal price;

    // 封面图片地址
    private String coverImg;

    // 库存数量（你原有已写，保留）
    private Integer stock;

    // 新增：发布唱片的卖家用户ID
    private Long sellerId;

    // 新增：商品状态 0下架 1上架
    private Integer status;

    // 新增：乐观锁版本号，用于防止库存超卖

    private Integer version;

    // 新增：逻辑删除 0未删 1已删
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    // 创建时间，新增自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间，新增&修改自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}