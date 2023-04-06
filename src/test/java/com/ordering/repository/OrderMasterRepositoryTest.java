package com.ordering.repository;

import com.ordering.dataobject.OrderMaster;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 *测试OrderMasterRepository
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMasterRepositoryTest {

    @Autowired
    private OrderMasterRepository repository;

    private final String OPENID = "110110";

    @Test
    public void saveTest() { //保存
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setOrderId("213567");
        orderMaster.setBuyerName("路飞子");
        orderMaster.setBuyerPhone("123456789123");
        orderMaster.setBuyerAddress("孔子大学");
        orderMaster.setBuyerOpenid(OPENID);
        orderMaster.setOrderAmount(new BigDecimal(2.5));
        //orderStatus 和 payStatus 设置成默认的值了 可以不传入.

        OrderMaster result = repository.save(orderMaster);
        Assert.assertNotNull(result);
    }

    @Test
    public void findByBuyerOpenid() throws Exception { //通过家的openid 查询这个人的订单
        PageRequest request = new PageRequest(0, 3);  //第0页  每一页显示三条记录 page从0开始的.

        Page<OrderMaster> result = repository.findByBuyerOpenid(OPENID, request);

        Assert.assertNotEquals(0,result.getTotalElements());
    }

}