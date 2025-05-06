package com.yupi.yupicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//在这里使用MapperScan注解用来扫描mapper包下的类，进行自动代理生成相应的实现类
@MapperScan("com.yupi.yupicturebackend.mapper")
// 通过Spring APO提供对当前代理对象的访问，使得可以在业务逻辑中访问到当前的代理对象，
// 可以在方法执行的时候通过AopContext.currentProxy()获取当前代理对象
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class YuPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuPictureBackendApplication.class, args);
    }

}
