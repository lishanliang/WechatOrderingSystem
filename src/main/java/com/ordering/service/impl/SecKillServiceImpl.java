package com.ordering.service.impl;

import com.ordering.exception.SellException;
import com.ordering.service.RedisLock;
import com.ordering.service.SecKillService;
import com.ordering.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 秒杀服务接口的实现类
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    private static final int TIMEOUT = 10 * 1000; //超时时间 10s

    @Autowired
    private RedisLock redisLock;

    /**
     * 暑假特价活动，冰激凌特价，限量100000份
     */
    static Map<String,Integer> products;
    static Map<String,Integer> stock;
    static Map<String,String> orders;
    static
    {
        /**
         * 模拟多个表，商品信息表，库存表，秒杀成功订单表
         */
        products = new HashMap<>(); //商品信息表
        stock = new HashMap<>();  //库存表
        orders = new HashMap<>(); //秒杀成功订单表
        products.put("123456", 100000);  //商品信息表中 商品id为123456的商品有100000件
        stock.put("123456", 100000);     //库存表中  商品id为123456的商品库存为100000件
    }

    private String queryMap(String productId) //独立出来的查询方法 根据productId查询信息 得到相应的信息
    {
        return "国庆活动，冰激凌特价，限量份"+ products.get(productId)
                +" 还剩：" + stock.get(productId)+" 份"
                +" 该商品成功下单用户数目：" +  orders.size() +" 人" ;
    }

    @Override
    // 查询秒杀活动特价商品的信息
    public String querySecKillProductInfo(String productId)
    {
        return this.queryMap(productId);
    }

    @Override
    //模拟不同用户秒杀同一商品的请求
    public void orderProductMockDiffUser(String productId)
    {
        //加锁
        long time=System.currentTimeMillis()+TIMEOUT;
        boolean resultRedis=redisLock.lock(productId,String.valueOf(time));
        if(!resultRedis){
            throw new SellException(101,"当前请求者过多，请稍后再试");
        }

        //1.查询该商品库存，为0则活动结束。
        int stockNum = stock.get(productId);
        if(stockNum == 0) {
            throw new SellException(100,"活动结束");
        }else {
            //2.下单(模拟不同用户  openid不同)
            orders.put(KeyUtil.genUniqueKey(),productId);//KeyUtil.genUniqueKey() 工具类生成随机的值作为openid
            //3.减库存
            stockNum =stockNum-1;
            try {
                Thread.sleep(100); //真实的减库存 会有IO操作等等，需要一定的时间，这里就模拟sleep 100 ms
                //注意这里减库存需要一定时间，在此期间别的并发线程还会继续下单  数据库中数据会不一致，必须加锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stock.put(productId,stockNum);
        }

        //解锁
        redisLock.unlock(productId,String.valueOf(time));
    }
}
