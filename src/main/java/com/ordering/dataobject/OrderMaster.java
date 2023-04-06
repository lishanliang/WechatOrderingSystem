package com.ordering.dataobject;

import com.ordering.enums.OrderStatusEnum;
import com.ordering.enums.PayStatusEnum;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *订单的主体 对应数据库中的order_master表
 */
@Entity
@Data
@DynamicUpdate
public class OrderMaster {

    /** 订单id. */
    @Id  //表示orderId为这个表的主键.
    private String orderId;

    /** 买家名字. */
    private String buyerName;

    /** 买家手机号. */
    private String buyerPhone;

    /** 买家地址. */
    private String buyerAddress;

    /** 买家微信Openid.  调试的时候直接在网页里设置一下也可 */
    private String buyerOpenid;

    /** 订单总金额. */
    private BigDecimal orderAmount;  //金额或价格  BigDecimal格式

    /** 订单状态, 默认为0新下单. */
    private Integer orderStatus = OrderStatusEnum.NEW.getCode();  //NEW为新订单

    /** 支付状态, 默认为0未支付. */
    private Integer payStatus = PayStatusEnum.WAIT.getCode(); //WAIT等待支付/未支付

    /** 创建时间. */
    private Date createTime;

    /** 更新时间. */
    private Date updateTime;

}
