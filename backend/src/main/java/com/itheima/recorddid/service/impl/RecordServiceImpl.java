package com.itheima.recorddid.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.recorddid.entity.Record;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.mapper.RecordMapper;
import com.itheima.recorddid.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * @author ldrnf
 * @description 针对表【record(唱片商品表)】的数据库操作Service实现
 * @createDate 2026-08-09 23:13:12
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
        implements RecordService {

    /**
     * 新增唱片：校验同一卖家不能重复上架同名唱片
     */
    @Override
    public void addRecord(Record record) {
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getRecordName, record.getRecordName())
                .eq(Record::getSellerId, record.getSellerId())
                .eq(Record::getDeleted, 0);
        Long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(500, "你已发布同名唱片，无法重复新增");
        }
        this.save(record);
    }

    /**
     * 修改唱片：校验唱片存在、归属当前卖家、同卖家不重名
     */
    @Override
    public void updateRecord(Record record, Long sellerId) {
        // 1. 判断唱片是否存在且未删除
        Record oldRecord = this.getById(record.getId());
        if (oldRecord == null || oldRecord.getDeleted() == 1) {
            throw new BusinessException(500, "要修改的唱片不存在");
        }

        // 2. 校验只能修改自己的唱片
        if (!oldRecord.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权修改他人发布的唱片");
        }

        // 3. 重名校验：排除自身id，同卖家不能有同名
        LambdaQueryWrapper<Record> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Record::getRecordName, record.getRecordName())
                .eq(Record::getSellerId, sellerId)
                .eq(Record::getDeleted, 0)
                .ne(Record::getId, record.getId());
        Long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(500, "该唱片名称已存在，无法修改");
        }

        // 4. 执行更新，乐观锁自动处理version
        boolean update = this.updateById(record);
        if (!update) {
            throw new BusinessException(500, "修改失败，数据已被他人更新，请刷新重试");
        }
    }
}