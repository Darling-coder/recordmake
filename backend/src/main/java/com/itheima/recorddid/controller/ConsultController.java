package com.itheima.recorddid.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.recorddid.common.Result;
import com.itheima.recorddid.entity.Consult;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.service.ConsultService;
import com.itheima.recorddid.common.UserContext;
import com.itheima.recorddid.vo.ConsultSendVO;
import com.itheima.recorddid.vo.ReplyConsultVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consult")
public class ConsultController {
    @Autowired
    private ConsultService consultService;

    /**
     * 买家发送咨询提问
     */
    @PostMapping("/send")
    public Result<String> send(@Valid @RequestBody ConsultSendVO vo){
        consultService.sendMsg(vo);
        return Result.success("咨询提交成功，请等待卖家回复");
    }

    /**
     * 卖家回复咨询
     */
    @PostMapping("/reply")
    public Result<String> reply(@Valid @RequestBody ReplyConsultVO vo){
        consultService.replyMsg(vo);
        return Result.success("回复发送成功");
    }

    /**
     * 买家：分页查询我的咨询记录，可根据唱片id筛选
     */
    @GetMapping("/buyer/list")
    public Result<IPage<Consult>> buyerList(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long recordId
    ){
        Page<Consult> page = new Page<>(pageNum, pageSize);
        IPage<Consult> pageData = consultService.getBuyerConsultPage(page, recordId);
        return Result.success(pageData);
    }

    /**
     * 卖家：分页查询本店全部咨询，可根据唱片id筛选
     */
    @GetMapping("/seller/list")
    public Result<IPage<Consult>> sellerList(
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long recordId
    ){
        Page<Consult> page = new Page<>(pageNum, pageSize);
        IPage<Consult> pageData = consultService.getSellerConsultPage(page, recordId);
        return Result.success(pageData);
    }

    /**
     * 根据咨询id查询单条详情（补充查看详情接口）
     */
    @GetMapping("/{id}")
    public Result<Consult> getDetail(@PathVariable Long id){
        Consult consult = consultService.getById(id);
        if(consult == null){
            throw new BusinessException(500, "咨询记录不存在");
        }
        // 权限校验：只能自己的咨询才能查看
        Long loginId = UserContext.getUserId();
        if(!consult.getBuyerId().equals(loginId) && !consult.getSellerId().equals(loginId)){
            throw new BusinessException(403, "无权查看该咨询");
        }
        return Result.success(consult);
    }
    // 查询当前登录卖家收到的全部咨询
    @GetMapping("/my")
    public Result getMyConsult(){
        Long sellerId = UserContext.getUserId();
        List<Consult> list = consultService.lambdaQuery()
                .eq(Consult::getSellerId, sellerId)
                .orderByDesc(Consult::getCreateTime)
                .list();
        return Result.success(list);
    }

}