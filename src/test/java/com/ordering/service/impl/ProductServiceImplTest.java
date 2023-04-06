package com.ordering.service.impl;

import com.ordering.dataobject.ProductInfo;
import com.ordering.enums.ProductStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    //因为这里findOne()和下面的Save()分别用了@Cacheable和@Cacheput，需要这两个方法的productId一样才行
    public void findOne() throws Exception {
        ProductInfo productInfo = productService.findOne("123460");
        System.out.println(productInfo.getProductId() +"   "+ productInfo.getProductPrice());
        Assert.assertEquals("123460", productInfo.getProductId());
    }

    @Test
    public void findUpAll() throws Exception {
        List<ProductInfo> productInfoList = productService.findUpAll();
        Assert.assertNotEquals(0, productInfoList.size());
    }

    @Test
    public void findAll() throws Exception {
        PageRequest request = new PageRequest(0, 2);//创建一个pageable对象. pageRequest是pageable接口的实现类
		//pageRequest实现了Pageable接口
        Page<ProductInfo> productInfoPage = productService.findAll(request);
//        System.out.println(productInfoPage.getTotalElements());
        Assert.assertNotEquals(0, productInfoPage.getTotalElements());
    }

    @Test
    public void save() throws Exception {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("123460");
        productInfo.setProductName("榴莲");
        productInfo.setProductPrice(new BigDecimal(19));
        productInfo.setProductStock(100);
        productInfo.setProductDescription("很香香的榴莲");
        productInfo.setProductIcon("http://xxxxx.jpg");
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());//这里为枚举 下架： ProductStatusEnum.DOWN.getCode()
        productInfo.setCategoryType(2);

        ProductInfo result = productService.save(productInfo);
        Assert.assertNotNull(result);
    }

//    @Test
//    public void onSale() {
//        ProductInfo result = productService.onSale("123456");
//        Assert.assertEquals(ProductStatusEnum.UP, result.getProductStatusEnum());
//    }
//
//    @Test
//    public void offSale() {
//        ProductInfo result = productService.offSale("123456");
//        Assert.assertEquals(ProductStatusEnum.DOWN, result.getProductStatusEnum());
//    }

}