package com.ordering.service;

import com.ordering.dto.OrderDTO;

/**
 * 买家 用到的BuyerService接口
 * 之前的findOne()只要传入一个orderId就可以查询，这个只要提供一个orderId就可以查询到别人的订单
 * 所以就先判断是不是本人的openid,再判断有没有这个订单orderId，这个逻辑在取消订单中也会用到，所以就写成一个实现类buyerService的方法findOrderOne()
 * 同理取消订单的判断写在了cancelOrder()
 */
public interface BuyerService {

    //查询一个订单
    OrderDTO findOrderOne(String openid, String orderId);

    //取消订单
    OrderDTO cancelOrder(String openid, String orderId);
}
