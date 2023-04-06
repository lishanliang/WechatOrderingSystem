package com.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching //Redis缓存使用的相关注解 属于Spring-context包 不用引入额外的依赖
//因为之前已经引入了Spring-boot-starter-web 而web依赖引用了Spring-web  Spring-web引用了Spring-context包
@SpringBootApplication
public class SellApplication {

	public static void main(String[] args) {SpringApplication.run(SellApplication.class, args);}
}
