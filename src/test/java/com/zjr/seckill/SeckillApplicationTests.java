package com.zjr.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisScript<Boolean> script;

    /**
     * Redis分布式锁基本原理
     * 执行一次这个方法，相当于模拟一个线程访问资源
     */
    @Test
    void contextLoads() {
        // 如果key不存在，才能设置成功
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("k1", "v1");
        // 如果占位成功，进行操作
        if (isLock) {
            System.out.println("do something....");
            // 操作结束，删除锁
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在占用锁");
        }
    }

    /**
     * Redis解决抛出异常导致的死锁
     * 使用过期键
     */
    @Test
    void contextLoads2() {
        // 如果key不存在，才能设置成功(防止抛出异常导致的死锁，添加锁的过期时间)
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        // 如果占位成功，进行操作
        if (isLock) {
            System.out.println("do something....");
            // 假设此时抛出了异常, 就会导致死锁，因为不会以下删除锁的语句不会执行
            Integer.parseInt("xx");
            // 操作结束，删除锁
            redisTemplate.delete("k1");
        } else {
            System.out.println("有线程在占用锁");
        }
    }

    /**
     * Redis lua脚本实现分布式锁
     * 加锁-判断锁-删锁
     */
    @Test
    void contextLoads3() {
        // 如果key不存在，才能设置成功(防止抛出异常导致的死锁，添加锁的过期时间)
        String value = UUID.randomUUID().toString();
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
        // 如果占位成功，进行操作
        if (isLock) {
            System.out.println("do something....");
            System.out.println("当前锁: " + redisTemplate.opsForValue().get("k1"));
            // 执行lua脚本
            Boolean result = redisTemplate.execute(script, Collections.singletonList("k1"), value);
            System.out.println(result);
        } else {
            System.out.println("有线程在占用锁");
        }
    }

}
