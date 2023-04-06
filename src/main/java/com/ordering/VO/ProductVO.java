package com.ordering.VO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品(包含类目)
 */
@Data
public class ProductVO implements Serializable {    //VO需要序列化 给前端或者Redis缓存的都是JSon格式

    //安装并使用插件GenerateSerialVersionUID 生成唯一的序列化ID   设置的快捷键为Ctrl+Shift+Q
    private static final long serialVersionUID = 7097863777546530545L;

    @JsonProperty("name")//但是返回给前端的还是name 在把对象序列化的时候返回给前端就是name了
    private String categoryName; //写的时候写具体一点的变量名 categoryName

    @JsonProperty("type")
    private Integer categoryType;

    @JsonProperty("foods")//商品的详情
    private List<ProductInfoVO> productInfoVOList;
}
