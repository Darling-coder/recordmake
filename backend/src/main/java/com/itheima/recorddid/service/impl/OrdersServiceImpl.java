package com.itheima.recorddid.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.recorddid.common.UserContext;
import com.itheima.recorddid.entity.Orders;
import com.itheima.recorddid.entity.Record;
import com.itheima.recorddid.entity.User;
import com.itheima.recorddid.exception.BusinessException;
import com.itheima.recorddid.mapper.OrdersMapper;
import com.itheima.recorddid.service.OrdersService;
import com.itheima.recorddid.service.RecordService;
import com.itheima.recorddid.vo.OrderCreateVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private RecordService recordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderCreateVO vo) {
        // 获取登录用户
        User loginUser = UserContext.getLoginUser();
        // 校验：仅买家可下单
        if (!"buyer".equals(loginUser.getRole())) {
            throw new BusinessException(403, "仅买家可下单购买唱片");
        }

        // 查询唱片是否存在、未逻辑删除
        Record record = recordService.getById(vo.getRecordId());
        if (record == null || record.getDeleted() == 1) {
            throw new BusinessException(500, "该唱片不存在或已下架");
        }
        // 定义buyNum变量
        Integer buyNum = vo.getBuyNum();
        // 2、校验库存是否足够
        if(record.getStock() < buyNum){
            throw new BusinessException(400,"库存不足，无法下单");
        }

        // 3、原子扣减库存（推荐，防超卖）
        UpdateWrapper<Record> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", vo.getRecordId())
                .ge("stock", buyNum)
                .setSql("stock = stock - " + buyNum);
        boolean updateSuccess = recordService.update(wrapper);
        if(!updateSuccess){
            throw new BusinessException(400,"库存不足，下单失败");
        }

        // 封装订单数据
        Orders orders = new Orders();
        orders.setRecordId(record.getId());
        orders.setBuyerId(loginUser.getId());
        orders.setSellerId(record.getSellerId());
        orders.setRecordName(record.getRecordName());
        orders.setPrice(record.getPrice());

        orders.setBuyNum(1);

        // record.getPrice() 单价 乘 buyNum 购买数量
        BigDecimal total = record.getPrice().multiply(new BigDecimal(vo.getBuyNum()));
        orders.setTotalPrice(total);
        orders.setOrderStatus(1); // 1=待付款
        // 新增：赋值收货地址
        orders.setAddress(vo.getAddress());
        save(orders);
    }

    @Override
    public List<Orders> getMyBuyOrder() {
        User loginUser = UserContext.getLoginUser();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getBuyerId, loginUser.getId())
                .eq(Orders::getDeleted, 0)
                .orderByDesc(Orders::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Orders> getMySellOrder() {
        User loginUser = UserContext.getLoginUser();
        if (!"seller".equals(loginUser.getRole())) {
            throw new BusinessException(403, "仅卖家可查看售卖订单");
        }
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getSellerId, loginUser.getId())
                .eq(Orders::getDeleted, 0)
                .orderByDesc(Orders::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 增加事务，全部成功/全部回滚
    public void cancelOrder(Long orderId) {
        User loginUser = UserContext.getLoginUser();
        Orders orders = getById(orderId);

        if (orders == null || orders.getDeleted() == 1) {
            throw new BusinessException(500, "订单不存在");
        }
        // 只能取消自己的订单
        if (!orders.getBuyerId().equals(loginUser.getId())) {
            throw new BusinessException(403, "无权取消他人订单");
        }
        // 仅待付款可取消
        if (orders.getOrderStatus() != 1) {
            throw new BusinessException(500, "仅待付款订单支持取消");
        }

        // 归还当前订单对应的唱片库存（单商品逻辑）
        UpdateWrapper<Record> stockWrapper = new UpdateWrapper<>();
        // 占位符写法，杜绝SQL注入
        stockWrapper.eq("id", orders.getRecordId())
                .setSql("stock = stock + {0}", orders.getBuyNum());
        recordService.update(stockWrapper);

        // 修改订单状态为已取消
        orders.setOrderStatus(3);
        updateById(orders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendGoods(Long orderId) {
        // 获取当前登录卖家
        User loginUser = UserContext.getLoginUser();
        Orders order = this.getById(orderId);

        // 1、校验订单是否存在、未删除
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException(500, "订单不存在");
        }

        // 2、校验操作人是该订单的卖家，买家不能发货
        if (!order.getSellerId().equals(loginUser.getId())) {
            throw new BusinessException(403, "仅订单所属卖家可执行发货操作");
        }

        // 3、状态限制：只有待付款(1)才能发货，已取消/已完成不可操作
        if (order.getOrderStatus() != 1) {
            throw new BusinessException(500, "当前订单状态不支持发货");
        }

        // 4、修改订单状态为【已发货】，状态码定义为2
        order.setOrderStatus(2);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId) {
        User loginUser = UserContext.getLoginUser();
        Orders order = this.getById(orderId);

        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException(500, "订单不存在");
        }

        // 只能订单买家确认收货
        if (!order.getBuyerId().equals(loginUser.getId())) {
            throw new BusinessException(403, "仅订单买家可确认收货");
        }

        // 必须是已发货(2)状态才能确认
        if (order.getOrderStatus() != 2) {
            throw new BusinessException(500, "订单尚未发货，无法确认收货");
        }

        // 修改状态为已完成 4
        order.setOrderStatus(4);
        this.updateById(order);
    }
}