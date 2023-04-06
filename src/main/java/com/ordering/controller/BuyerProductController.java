package com.ordering.controller;

import com.ordering.VO.ProductInfoVO;
import com.ordering.VO.ProductVO;
import com.ordering.VO.ResultVO;
import com.ordering.dataobject.ProductCategory;
import com.ordering.dataobject.ProductInfo;
import com.ordering.service.CategoryService;
import com.ordering.service.ProductService;
import com.ordering.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 买家端商品是查询为主  查询商品信息展示给买家看
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    @Autowired
    private ProductService productService;  //查商品

    @Autowired
    private CategoryService categoryService; //查类目

    @GetMapping("/list")
    @Cacheable(cacheNames = "product",key = "123")  //Redis缓存的使用  @Cacheable只执行一次list()中代码 查询结果放入缓存 之后会从缓存中取
// 动态设置key  @Cacheable(cacheNames = "product", key = "#sellerId", condition = "#sellerId.length() > 3", unless = "#result.getCode() != 0")
    //记得在数据库数据更新的时候 用@CachePut(cacheNames="product",key="123")  更新的时候必须把更新的写到缓存中去 cacheput每次都会执行一次  key要相等的
    public ResultVO list(@RequestParam(value = "sellerId", required = false) String sellerId) {
        //第一步和第二步都是数据库的查询，建议放在最前面不要放到for循环中，性能会更快

        //1. 查询所有的上架商品 没有用到翻页所以直接查询所有的在架的商品
        List<ProductInfo> productInfoList = productService.findUpAll();

        //2. 查询类目(一次性查询:查一次出结果)
        List<Integer> categoryTypeList = new ArrayList<>();
        //传统方法
        for (ProductInfo productInfo : productInfoList) {
            categoryTypeList.add(productInfo.getCategoryType());
        }

        //根据查出的类目类型(如6)找到相应的商品类目信息(6对应的一条或多条的商品类目记录)
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);

        //3. 数据拼装 对照API文档中前端页面格式
        List<ProductVO> productVOList = new ArrayList<>();//最外层的列表 对应data的内容
        for (ProductCategory productCategory: productCategoryList) { //先遍历类目
            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(productCategory.getCategoryName()); //categoryName 对应第二层的name
            productVO.setCategoryType(productCategory.getCategoryType()); //categoryType 对应第二层的type

            List<ProductInfoVO> productInfoVOList = new ArrayList<>();//里层的列表 对应foods的内容
            for (ProductInfo productInfo: productInfoList) {         //再遍历上架的商品详情
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {  //根据type找相应商品:看商品的type能不能对上了
                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    BeanUtils.copyProperties(productInfo, productInfoVO); //属性太多了，直接用Spring提供的BeanUtils类中的copyProperties方法实现属性的拷贝
                    productInfoVOList.add(productInfoVO);
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);//productInfoVOList 对应foods 把foods加入对应的data
            productVOList.add(productVO);//一个data拼接好了
        }

//        ResultVO resultVO=new ResultVO();
//        resultVO.setData(productVOList);
//        resultVO.setCode(0);
//        resultVO.setMsg("成功"); 不用这种朴素的方法写，因为后面要是有别的接口用resultVO对象都得这么写 不如封装一下见ResultVOUtil.java

        return ResultVOUtil.success(productVOList); //将data填入resultVO中，同时设置好ResultVO的code和msg
    }
}
