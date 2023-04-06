package com.ordering.repository;

import com.ordering.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 *商品的类目  主要是JPA
 */
@RunWith(SpringRunner.class) //单元测试
@SpringBootTest
public class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository repository;

    @Test
    public void findOneTest() {//按照字段查询一个记录
        ProductCategory productCategory = repository.findOne(1);
        System.out.println(productCategory.toString());
    }

    @Test
//    @Transactional  //测试过之后 数据库不会留下测试数据   测试里面的事物Transactional是完全回滚
    public void saveTest() { //新增一条数据到数据库中
        ProductCategory productCategory = new ProductCategory("男生最爱", 5); //categoryId会自增不需要写
        ProductCategory result = repository.save(productCategory);
        Assert.assertNotNull(result);
//        Assert.assertNotEquals(null, result);
    }

    @Test
    public void findByCategoryTypeInTest() { //通过类目的多个id将所有类目都找出来
        List<Integer> list = Arrays.asList(2,3,4);

        List<ProductCategory> result = repository.findByCategoryTypeIn(list);
        Assert.assertNotEquals(0, result.size());
    }

    @Test
    public void updateTest() { //更新 也是用save方法 不过需要先设置主键
        ProductCategory productCategory = repository.findOne(8);  //需要先查出来 先验证信息 看数据对不对
        productCategory.setCategoryName("男孩最爱"); //再进行修改  如果查出来的记录和你要求修改的记录值一样  其实是不会改的
        repository.save(productCategory); //保存修改更新了
    }
}