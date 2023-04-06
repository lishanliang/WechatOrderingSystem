package com.ordering.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ordering.dataobject.OrderDetail;
import com.ordering.dto.OrderDTO;
import com.ordering.enums.ResultEnum;
import com.ordering.exception.SellException;
import com.ordering.form.OrderForm;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderForm 转换为 OrderDTO
 */
@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        Gson gson = new Gson();
        OrderDTO orderDTO = new OrderDTO();
        //同一个字段在两张表中的字段名不一样  不能直接用BeanUtils的copyProperties()
        orderDTO.setBuyerName(orderForm.getName());  //姓名
        orderDTO.setBuyerPhone(orderForm.getPhone()); //手机号码
        orderDTO.setBuyerAddress(orderForm.getAddress());//地址
        orderDTO.setBuyerOpenid(orderForm.getOpenid());//openId

        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(),  //第一个参数是待转换的数据    购物车信息Items其实是一个字符串 从字符串转为Json格式用Gson
                    new TypeToken<List<OrderDetail>>(){}.getType()); //第二个参数是需要转换成的类型 需要转换为List<OrderDetail>类型.
        } catch (Exception e) {
            log.error("【对象转换】错误, string={}", orderForm.getItems());
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        orderDTO.setOrderDetailList(orderDetailList);//购物车里的内容

        return orderDTO; //由orderMaster转来的orderDTO  返回
    }
}
