package com.itheima.recorddid.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.recorddid.entity.Orders;
import com.itheima.recorddid.vo.OrderCreateVO;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    // 买家下单
    void createOrder(OrderCreateVO vo);

    // 买家：查询我的全部购买订单
    List<Orders> getMyBuyOrder();

    // 卖家：查询我所有唱片的售卖订单
    List<Orders> getMySellOrder();

    // 买家取消未付款订单
    void cancelOrder(Long orderId);

    /**
     * 卖家发货操作
     * @param orderId 订单id
     */
    void sendGoods(Long orderId);

    /**
     * 买家确认收货，订单完结
     * @param orderId 订单id
     */
    void confirmReceive(Long orderId);
}