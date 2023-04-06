package com.ordering.service;

import com.ordering.dataobject.ProductInfo;
//import com.ordering.dto.CartDTO;
import com.ordering.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 商品
 *
 */
public interface ProductService {

    ProductInfo findOne(String productId);

    /**
     * 查询所有在架商品列表 买家端和管理端都可以用
     * @return
     */
    List<ProductInfo> findUpAll();

    Page<ProductInfo> findAll(Pageable pageable); //管理端 查询所有商品，注意要分页

    ProductInfo save(ProductInfo productInfo);

    //加库存
    void increaseStock(List<CartDTO> cartDTOList);

    //减库存
    void decreaseStock(List<CartDTO> cartDTOList);

//    //上架
//    ProductInfo onSale(String productId);
//
//    //下架
//    ProductInfo offSale(String productId);
}
