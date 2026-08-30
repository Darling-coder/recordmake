package com.itheima.recorddid.controller;

import com.itheima.recorddid.common.Result;
import com.itheima.recorddid.entity.Orders;
import com.itheima.recorddid.service.OrdersService;
import com.itheima.recorddid.vo.OrderCreateVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 买家下单
     */
    @PostMapping("/create")
    public Result<String> create(@Valid @RequestBody OrderCreateVO vo) {
        ordersService.createOrder(vo);
        return Result.success("下单成功");
    }

    /**
     * 买家：我的购买订单列表
     */
    @GetMapping("/buy/list")
    public Result<List<Orders>> myBuyOrder() {
        List<Orders> list = ordersService.getMyBuyOrder();
        return Result.success(list);
    }

    /**
     * 卖家：我的售卖订单列表
     */
    @GetMapping("/sell/list")
    public Result<List<Orders>> mySellOrder() {
        List<Orders> list = ordersService.getMySellOrder();
        return Result.success(list);
    }

    /**
     * 买家取消待付款订单
     */
    @PutMapping("/cancel/{OrderId}")
    public Result<String> cancel(@PathVariable Long orderId) {
        ordersService.cancelOrder(orderId);
        return Result.success("订单已取消");
    }

    /**
     * 卖家发货接口
     */
    @PutMapping("/send/{orderId}")
    public Result<String> send(@PathVariable Long orderId) {
        ordersService.sendGoods(orderId);
        return Result.success("发货操作完成，订单状态更新为已发货");
    }

    /**
     * 买家确认收货
     */
    @PutMapping("/confirm/{orderId}")
    public Result<String> confirm(@PathVariable Long orderId) {
        ordersService.confirmReceive(orderId);
        return Result.success("确认收货成功，订单已完成");
    }
}