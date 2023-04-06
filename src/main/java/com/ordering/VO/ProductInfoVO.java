package com.ordering.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品详情 是从ProductInfo中抽取出来的几个字段 是为了隐私(只讲必要的信息给前端，后台的数据尽可能对外不可见更安全)
 *
 */
@Data
public class ProductInfoVO implements Serializable {//VO需要序列化 给前端或者Redis缓存的都是JSon格式

    //安装并使用插件GenerateSerialVersionUID 生成唯一的序列化ID   设置的快捷键为Ctrl+Shift+Q
    private static final long serialVersionUID = -3895834204864685262L;

    @JsonProperty("id")
    private String productId;

    @JsonProperty("name")
    private String productName;

    @JsonProperty("price")
    private BigDecimal productPrice;

    @JsonProperty("description")
    private String productDescription;

    @JsonProperty("icon")
    private String productIcon;
}
