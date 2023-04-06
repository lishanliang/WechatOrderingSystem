package com.ordering.controller;

import com.ordering.VO.ResultVO;
import com.ordering.converter.OrderForm2OrderDTOConverter;
import com.ordering.dto.OrderDTO;
import com.ordering.enums.ResultEnum;
import com.ordering.exception.SellException;
import com.ordering.form.OrderForm;
import com.ordering.service.BuyerService;
import com.ordering.service.OrderService;
import com.ordering.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家订单的api
 */
@RestController//前端返回的是restful风格的内容
@RequestMapping("/buyer/order")
@Slf4j //日志
public class BuyerOrderController {

    @Autowired
    private OrderService orderService; //订单服务类

    @Autowired
    private BuyerService buyerService;

    //创建订单
    @PostMapping("/create") //路径  前端/postman的买家发送订单过来 测试用 post 127.0.0.1:8080/sell/buyer/order/create
    public ResultVO<Map<String, String>> create(@Valid OrderForm orderForm,//参数太多了，就建一个对象orderForm用于传参
                                                BindingResult bindingResult) {//orderForm自定义用于表单校验，BindingResult为表单类
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());//具体的出错的信息  触发异常时候的错误信息
        }

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);//对象的转换 orderForm转换为orderDTO  同理写一个专门的转换器/类
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO createResult = orderService.create(orderDTO);//这里create方法需要传入orderDTO类型的对象

        Map<String, String> map = new HashMap<>();
        map.put("orderId", createResult.getOrderId());


        //  ResultVO resultVO=new ResultVO();
        //  resultVO.setData(map);
        //  resultVO.setCode(0);
        //  resultVO.setMsg("成功"); 不用这种朴素的方法写，因为后面要是有别的接口用resultVO对象都得这么写 不如封装一下见ResultVOUtil.java
        return ResultVOUtil.success(map); //将data即map填入resultVO中，同时设置好ResultVO的code和msg
    }

    //订单列表
    @GetMapping("/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,  //参数就三个 不多  直接写就好
                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (StringUtils.isEmpty(openid)) {   //校验:openid不能为空
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }

        PageRequest request = new PageRequest(page, size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid, request);

        //  ResultVO resultVO=new ResultVO();
        //  resultVO.setData(orderDTOPage.getContent());
        //  resultVO.setCode(0);
        //  resultVO.setMsg("成功"); 不用这种朴素的方法写，因为后面要是有别的接口用resultVO对象都得这么写 不如封装一下见ResultVOUtil.java
        return ResultVOUtil.success(orderDTOPage.getContent());
    }


    //订单详情
    @GetMapping("/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openid") String openid,
                                     @RequestParam("orderId") String orderId) {
        OrderDTO orderDTO = buyerService.findOrderOne(openid, orderId);//安全性  之前的findOne()只要传入一个orderId就可以查询，这个不安全
        //所以就先判断是不是本人的openid,再判断有没有这个订单orderId，这个逻辑在取消订单中也会用到，所以就写成一个实现类buyerService的方法findOrderOne()
        //尽量把处理逻辑放到Service层去做
        return ResultVOUtil.success(orderDTO);
    }

    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
        buyerService.cancelOrder(openid, orderId);//这里同理  不安全：之前的取消方法只需要参数orderId，没有先判断openid看是不是本人的订单，可能会把别人的订单信息泄露
        //这里同样把判断方法写在buyerService的方法cancelOrder()中
        return ResultVOUtil.success();
    }
}
