package com.itheima.recorddid.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.recorddid.entity.Consult;
import com.itheima.recorddid.vo.ConsultSendVO;
import com.itheima.recorddid.vo.ReplyConsultVO;

public interface ConsultService extends IService<Consult> {

    /**
     * 买家提交咨询
     */
    void sendMsg(ConsultSendVO vo);

    /**
     * 卖家回复咨询
     */
    void replyMsg(ReplyConsultVO vo);

    /**
     * 买家分页查询自己的咨询，支持按唱片筛选
     */
    IPage<Consult> getBuyerConsultPage(Page<Consult> page, Long recordId);

    /**
     * 卖家分页查询本店唱片咨询，支持按唱片筛选
     */
    IPage<Consult> getSellerConsultPage(Page<Consult> page, Long recordId);
}