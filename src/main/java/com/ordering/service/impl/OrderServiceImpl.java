package com.ordering.service.impl;

import com.ordering.converter.OrderMaster2OrderDTOConverter;
import com.ordering.dataobject.OrderDetail;
import com.ordering.dataobject.OrderMaster;
import com.ordering.dataobject.ProductInfo;
import com.ordering.dto.CartDTO;
import com.ordering.dto.OrderDTO;
import com.ordering.enums.OrderStatusEnum;
import com.ordering.enums.PayStatusEnum;
import com.ordering.enums.ResultEnum;
import com.ordering.exception.SellException;
import com.ordering.repository.OrderDetailRepository;
import com.ordering.repository.OrderMasterRepository;
import com.ordering.service.*;
import com.ordering.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

//    @Autowired
//    private PayService payService;
//
//    @Autowired
//    private PushMessageService pushMessageService;
//
//    @Autowired
//    private WebSocket webSocket;

    @Override
    @Transactional  //普通的事物  保证原子性   出现异常会回滚
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey(); //生成随机数 作为id
        BigDecimal orderAmount = new BigDecimal(0);  //自己定义的总价

        //1. 查询商品（数量, 价格）  价格和数量都必须按照商品id从数据库中查询 不能从前端传回来价格（不安全）
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {  //orderDTO.getOrderDetailList()订单详情中的多条订单信息
            ProductInfo productInfo =  productService.findOne(orderDetail.getProductId()); //查出商品详情
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
        //2. 计算订单总价 = 商品单价*订单中商品的数量 + 之前的总价
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                    .add(orderAmount);   //BigDecimal有专门的计算方法 .multiply .add

            //订单详情入库
            orderDetail.setDetailId(KeyUtil.genUniqueKey()); //生成随机数 作为id
            orderDetail.setOrderId(orderId); //生成随机数 作为id
            BeanUtils.copyProperties(productInfo, orderDetail); //属性拷贝
            orderDetailRepository.save(orderDetail);
        }

        //3. 写入订单数据库（orderMaster和orderDetail,orderDetail在上面已经入库）
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId); //因为后面的买家订单api的创建订单方法中需要用这个create方法 需要orderDTO中也有这个id值 所以这里设置一下
        BeanUtils.copyProperties(orderDTO, orderMaster); //注意先拷贝属性 然后再设置相应的属性  不然会覆盖已经设置好的属性的
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode()); //默认的订单状态和支付状态 在属性拷贝之后被覆盖为null，需要重新设置
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        //4. 扣库存   可能会有多线程并发的问题
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map( e ->
                new CartDTO(e.getProductId(), e.getProductQuantity())
        ).collect( Collectors.toList() );    //每个CartDTO只有两个属性ProductId，ProductQuantity
        productService.decreaseStock(cartDTOList);

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) { //查询单个订单

        OrderMaster orderMaster = orderMasterRepository.findOne(orderId); //订单主体中看订单是否存在
        if (orderMaster == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId); //看订单详情
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO(); //订单的DTO orderDTO
        BeanUtils.copyProperties(orderMaster, orderDTO);//属性拷贝
        orderDTO.setOrderDetailList(orderDetailList);//属性的设置 设置订单详情的列表

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) { //查询订单的列表
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<OrderDTO>(orderDTOList, pageable, orderMasterPage.getTotalElements());//返回的是page对象
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) { //取消订单

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {  //订单状态不是 新建的/新下单，就报错
            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态  订单的状态已经是新建的，可以取消了
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        OrderMaster orderMaster = new OrderMaster();  //转换一下类型  orderDTO变为orderMaster
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //返还库存
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) { //先判断一下商品详情是否为空
            log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()   //和之前一样  建立CartDTO列表
                .map(e -> new CartDTO(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        //如果已支付, 需要退款

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) { //完结订单
        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) { //订单状态不是 新建的/新下单，就报错
            log.error("【完结订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        OrderMaster orderMaster = new OrderMaster(); //转换一下类型  orderDTO变为orderMaster
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {    //判断一下更新结果
            log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

//        //推送微信模版消息
//        pushMessageService.orderStatus(orderDTO);

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(OrderDTO orderDTO) {   //支付订单
        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) { //订单状态不是 新建的/新下单，就报错
            log.error("【订单支付完成】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //判断支付状态
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) { //支付状态不是  待支付，就报错
            log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改支付状态  订单是新建的 而且是未支付的  就可以修改订单的支付状态了.
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        OrderMaster orderMaster = new OrderMaster(); //转换一下类型  orderDTO变为orderMaster
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【订单支付完成】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }
}
