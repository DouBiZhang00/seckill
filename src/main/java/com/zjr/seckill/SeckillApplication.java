package com.zjr.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zjr.seckill.mapper")
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }

}
