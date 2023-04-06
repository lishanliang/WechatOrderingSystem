package com.ordering.utils;

import java.util.Random;

/**
 *生成随机数的方法
 */
public class KeyUtil {

    /**
     * 生成唯一的主键
     * 格式: 时间+随机数
     * @return
     */
    public static synchronized String genUniqueKey() {  //synchronized保证生成Id的唯一性
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000; //随机数的位数固定  都为六位数

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
