package com.ordering.service;

import com.ordering.dataobject.ProductCategory;

import java.util.List;

/**
 * 类目的服务层
 */
public interface CategoryService {

    ProductCategory findOne(Integer categoryId); //通过id找对应的类目

    List<ProductCategory> findAll();//后台管理需要查询全部的类目

    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);//买家端需要通过商品类型来查找商品类目

    ProductCategory save(ProductCategory productCategory);//保存
}
