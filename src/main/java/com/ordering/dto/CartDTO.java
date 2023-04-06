package com.ordering.dto;

import lombok.Data;

/**
 * 购物车
 *对应API文档中的购物车
 * items: [{
 *   productId: "1423113435324",
 *   productQuantity: 2 //购买数量
 * }]
 * 前端一共就两个字段
 */
@Data
public class CartDTO {

    /** 商品Id. */
    private String productId;

    /** 数量. */
    private Integer productQuantity;

    public CartDTO(String productId, Integer productQuantity) {
        this.productId = productId;
        this.productQuantity = productQuantity;
    }
}
