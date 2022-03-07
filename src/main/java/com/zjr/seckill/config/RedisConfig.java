package com.zjr.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    /**
     * 序列化redis操作类redisTemplate
     * @param redisConnectionFactory redis的键一般都是字符串String
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        // 序列化redis的key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 序列化value
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 序列化hash的key, 因为它特殊, hash中也有key-value
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 序列化value
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Boolean> script() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("lock.lua"));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }
}
