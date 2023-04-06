package com.ordering.repository;

import com.ordering.dataobject.OrderDetail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 *测试 OrderDetailRepository
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository repository;

    @Test
    public void saveTest() {  //新增  保存数据的方法
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setDetailId("1234567811");
        orderDetail.setOrderId("11111117");
        orderDetail.setProductIcon("http://p2.jpg");
        orderDetail.setProductId("11111112");
        orderDetail.setProductName("阿牛的奶茶");
        orderDetail.setProductPrice(new BigDecimal(2.2));
        orderDetail.setProductQuantity(3);

        OrderDetail result = repository.save(orderDetail);
        Assert.assertNotNull(result);
    }

    @Test
    public void findByOrderId() throws Exception { //根据orderId查询具体的订单信息
        List<OrderDetail> orderDetailList = repository.findByOrderId("11111117");
        Assert.assertNotEquals(0, orderDetailList.size());
    }

}