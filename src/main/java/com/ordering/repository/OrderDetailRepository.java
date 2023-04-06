package com.ordering.repository;

import com.ordering.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *订单详情表中数据的查询  根据在订单主体表中查到的信息 来订单详情表中进一步得到买家的订单信息
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    List<OrderDetail> findByOrderId(String orderId); //按照orderId找到具体订单信息 一位买家可能会有多笔订单
}
