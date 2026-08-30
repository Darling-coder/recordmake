package com.itheima.recorddid.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增数据时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 逻辑删除默认 0（未删除）
        this.strictInsertFill(metaObject, "deleted", () -> 0, Integer.class);
        // 乐观锁版本号默认 0
        this.strictInsertFill(metaObject, "version", () -> 0, Integer.class);
    }

    /**
     * 更新数据时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改时自动刷新更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}