package com.ordering.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 实现Redis分布式锁
 */
@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate; //引入 StringRedisTemplate类写分布式锁

    /**
     * 加锁
     * @param key  productId
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value) {
        // 1 当前这把锁key还不存在/可以设置的话  当前线程直接获得这把锁 true
        if(redisTemplate.opsForValue().setIfAbsent(key, value)) { //Setnx:set if Not exists = setIfAbsent(key, value)  /表示key不存在/缺席就设置值 setIfAbsent返回true，key值存在就啥都不做 setIfAbsent返回false
            return true; //true表示可以设置key对应的value，已经被锁住了
        }

        // 2 当前这把锁无法设置 锁已经被其他线程获取； 就需要看看这把锁有没有过期（使用GetSet方法）
        // /假设currentValue=A   同时来的两个线程传入的参数value都是B  如果锁过期了那么其中一个线程一定可以拿到锁，从而避免锁过期导致的死锁
        String currentValue = redisTemplate.opsForValue().get(key); //获取key的值
        //如果锁过期
        if (!StringUtils.isEmpty(currentValue)  //该线程的时间值不为空 且 线程的时间值小于当前时间  说明这个线程的锁过期了。
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);//GetSet:先get 再set = getAndSet(key, value)
            // 这个GetSet一次只会被一个线程执行 总归有个先后的
            // 先执行GetSet方法的线程获得的oldValue为A       此时oldValue==currentValue==A      return true(先执行GetSet方法的线程获得锁)
            // 后面的线程 再去执行GetSet获得的oldValue会是B  那么oldValue(B)!=currentValue(A)   return false(后面执行的并发线程不会得到锁)
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) { //当前上一个锁对应的时间不为空 且 上一个锁的时间等于当前key对应的时间值
                return true; //第一个执行GetSet线程获得锁 解除死锁
            }
        }


        // 3 当前这把锁已经被其他线程获取 而且这把锁还没有过期  那当前请求锁的线程就得等着 false
        return false;
    }

    /**
     * 解锁
     * @param key productId
     * @param value  当前时间+超时时间
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);  //获取锁的当前时间值
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) { //时间值不为空 且 锁的当前时间值==value(当前时间+超时时间)
                redisTemplate.opsForValue().getOperations().delete(key);  //就解锁 即删除key
            }
        }catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }

}
