package com.ordering.service.impl;

import com.ordering.dto.OrderDTO;
import com.ordering.enums.ResultEnum;
import com.ordering.exception.SellException;
import com.ordering.service.BuyerService;
import com.ordering.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 实现买家用到的BuyerService接口
 * 之前的findOne()只要传入一个orderId就可以查询，这个只要提供一个orderId就可以查询到别人的订单
 * 所以就先判断是不是本人的openid,再判断有没有这个订单orderId，这个逻辑在取消订单中也会用到，所以就写成一个实现类buyerService的方法findOrderOne()
 * 同理取消订单的判断写在了cancelOrder()
 */
@Service
@Slf4j
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private OrderService orderService;

    @Override
    public OrderDTO findOrderOne(String openid, String orderId) {
        return checkOrderOwner(openid, orderId);
    }

    @Override
    public OrderDTO cancelOrder(String openid, String orderId) {
        OrderDTO orderDTO = checkOrderOwner(openid, orderId);
        if (orderDTO == null) {
            log.error("【取消订单】查不到该订单, orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        return orderService.cancel(orderDTO);
    }

    private OrderDTO checkOrderOwner(String openid, String orderId) { //判断这个订单orderId是不是这个人openid的.
        OrderDTO orderDTO = orderService.findOne(orderId);//用orderId查找出来订单DTO(orderDTO中有订单的多个详细信息)
        if (orderDTO == null) {
            return null;
        }
        //判断是否是自己的订单
        if (!orderDTO.getBuyerOpenid().equalsIgnoreCase(openid)) { //当前访问者的openid对应的orderId  和  他查询的订单的orderId 不相同就报错.
            log.error("【查询订单】订单的openid不一致. openid={}, orderDTO={}", openid, orderDTO);
            throw new SellException(ResultEnum.ORDER_OWNER_ERROR);
        }
        return orderDTO;
    }
}
