package com.itheima.recorddid.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.recorddid.entity.Consult;
import com.itheima.recorddid.entity.Record;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.mapper.ConsultMapper;
import com.itheima.recorddid.service.ConsultService;
import com.itheima.recorddid.service.RecordService;
import com.itheima.recorddid.common.UserContext;
import com.itheima.recorddid.vo.ConsultSendVO;
import com.itheima.recorddid.vo.ReplyConsultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ConsultServiceImpl extends ServiceImpl<ConsultMapper, Consult> implements ConsultService {

    @Autowired
    private RecordService recordService;

    @Override
    public void sendMsg(ConsultSendVO vo) {
        Long buyerId = UserContext.getUserId();
        // 校验唱片是否存在
        Record record = recordService.getById(vo.getRecordId());
        if (record == null) {
            throw new BusinessException(500, "目标唱片不存在，无法发起咨询");
        }
        // 组装咨询数据
        Consult consult = new Consult();
        consult.setRecordId(vo.getRecordId());
        consult.setBuyerId(buyerId);
        consult.setSellerId(record.getSellerId());
        consult.setQuestion(vo.getQuestion());
        // createTime、updateTime 自动填充无需手动赋值
        save(consult);
    }

    @Override
    public void replyMsg(ReplyConsultVO vo) {
        Long sellerId = UserContext.getUserId();
        Consult consult = getById(vo.getConsultId());
        // 校验咨询记录
        if (consult == null) {
            throw new BusinessException(500, "该咨询记录不存在");
        }
        // 权限校验：只能回复自己店铺唱片的咨询
        if (!consult.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权回复他人店铺的咨询");
        }
        // 更新回复内容与回复时间
        consult.setReply(vo.getReplyContent());
        consult.setReplyTime(LocalDateTime.now());
        updateById(consult);
    }

    @Override
    public IPage<Consult> getBuyerConsultPage(Page<Consult> page, Long recordId) {
        Long buyerId = UserContext.getUserId();
        LambdaQueryWrapper<Consult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consult::getBuyerId, buyerId);
        // 可选：按唱片id过滤
        if (recordId != null) {
            wrapper.eq(Consult::getRecordId, recordId);
        }
        // 按创建时间倒序，最新咨询在前
        wrapper.orderByDesc(Consult::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public IPage<Consult> getSellerConsultPage(Page<Consult> page, Long recordId) {
        Long sellerId = UserContext.getUserId();
        LambdaQueryWrapper<Consult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Consult::getSellerId, sellerId);
        if (recordId != null) {
            wrapper.eq(Consult::getRecordId, recordId);
        }
        wrapper.orderByDesc(Consult::getCreateTime);
        return page(page, wrapper);
    }
}