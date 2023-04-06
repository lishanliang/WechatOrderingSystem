package com.ordering.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ordering.dataobject.OrderDetail;
import com.ordering.enums.OrderStatusEnum;
import com.ordering.enums.PayStatusEnum;
import com.ordering.utils.EnumUtil;
import com.ordering.utils.serializer.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Data transfer object 数据传输对象 用于在各层之间传输数据的
 * 由于订单主体中一条记录可能对应订单详情中多条数据 要有 List<OrderDetail> orderDetailList;
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)       //严格来说有些不是必须的字段 如果是NULL就不返回给前端
public class OrderDTO {

    /** 订单id. */
    private String orderId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid. */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;

    /** 订单状态, 默认为0新下单. */
    private Integer orderStatus;

    /** 支付状态, 默认为0未支付. */
    private Integer payStatus;

    /** 创建时间. */
    @JsonSerialize(using = Date2LongSerializer.class)//这里使用了Date2LongSerializer类 将Date类型转换为Long  因为前端get到的时间和后台数据库的时间类型不一样 时间多了三个零
    private Date createTime;

    /** 更新时间. */
    @JsonSerialize(using = Date2LongSerializer.class)  //这里使用了Date2LongSerializer类 将Date类型转换为Long
    private Date updateTime;

    List<OrderDetail> orderDetailList; //对应买家购物车中购买的商品即CartDTO==OrderDetail  每一个OrderDetail中只有两个值(商品id 商品数量)

    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(orderStatus, OrderStatusEnum.class);
    }

    @JsonIgnore
    public PayStatusEnum getPayStatusEnum() {
        return EnumUtil.getByCode(payStatus, PayStatusEnum.class);
    }
}
