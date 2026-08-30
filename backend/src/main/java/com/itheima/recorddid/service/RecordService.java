package com.itheima.recorddid.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.recorddid.entity.Record;

/**
 * @author ldrnf
 * @description 针对表【record(唱片商品表)】的数据库操作Service
 * @createDate 2026-08-09 23:13:12
 */
public interface RecordService extends IService<Record> {

    /**
     * 新增唱片业务逻辑（校验同卖家唱片名唯一）
     */
    void addRecord(Record record);

    /**
     * 修改唱片业务逻辑（校验归属、重名）
     * @param record 前端传入的唱片数据
     * @param sellerId 当前登录卖家id
     */
    void updateRecord(Record record, Long sellerId);
}