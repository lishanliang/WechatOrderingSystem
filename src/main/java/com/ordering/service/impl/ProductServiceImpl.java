package com.ordering.service.impl;

//import com.ordering.config.UpYunConfig;
import com.ordering.dataobject.ProductInfo;
//import com.ordering.dto.CartDTO;
import com.ordering.dto.CartDTO;
import com.ordering.enums.ProductStatusEnum;
import com.ordering.enums.ResultEnum;
import com.ordering.exception.SellException;
import com.ordering.repository.ProductInfoRepository;
import com.ordering.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository repository;

    @Override
    @Cacheable(cacheNames = "product",key = "123")  //Redis缓存的使用  @Cacheable只执行一次findOne()中代码 将查询结果放入缓存 之后会直接从缓存中取
    public ProductInfo findOne(String productId) {
       return repository.findOne(productId);
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return repository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return  repository.findAll(pageable);
    }

    @Override
    @CachePut(cacheNames = "product",key = "123")  // @CachePut() 每次都会执行  每次都会把save中更新数据内容写入redis缓存  即为增量更新
    //注意这里增量更新会按照key值来匹配 所以@Cacheable和@CachePut的key要相同
    public ProductInfo save(ProductInfo productInfo) {
        return repository.save(productInfo);
    }

    @Override
    @Transactional  //保证事物的原子性
    public void increaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO: cartDTOList) {
            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            productInfo.setProductStock(result);

            repository.save(productInfo);
        }

    }

    @Override
    @Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for (CartDTO cartDTO: cartDTOList) {
            ProductInfo productInfo = repository.findOne(cartDTO.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            Integer result = productInfo.getProductStock() - cartDTO.getProductQuantity(); //商品的库存-购物车中买家需要购买的商品数量
            if (result < 0) {
                throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }

            productInfo.setProductStock(result);  //商品存在且库存足够多 就可以扣库存了

            repository.save(productInfo);
        }
    }
//
//    @Override
//    public ProductInfo onSale(String productId) {
//        ProductInfo productInfo = repository.findById(productId).orElse(null);
//        if (productInfo == null) {
//            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
//        }
//        if (productInfo.getProductStatusEnum() == ProductStatusEnum.UP) {
//            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
//        }
//
//        //更新
//        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
//        return repository.save(productInfo);
//    }
//
//    @Override
//    public ProductInfo offSale(String productId) {
//        ProductInfo productInfo = repository.findById(productId).orElse(null);
//        if (productInfo == null) {
//            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
//        }
//        if (productInfo.getProductStatusEnum() == ProductStatusEnum.DOWN) {
//            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
//        }
//
//        //更新
//        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
//        return repository.save(productInfo);
//    }
}
