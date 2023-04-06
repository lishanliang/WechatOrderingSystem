package com.ordering.repository;

import com.ordering.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *在订单的主体表中 根据买家的信息查询出这位买家的订单详情 去订单详情表中查询具体信息
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);
    //根据买家的openid 查询这个人的订单  为了限制每页显示数量会有一个分页pageable
}
