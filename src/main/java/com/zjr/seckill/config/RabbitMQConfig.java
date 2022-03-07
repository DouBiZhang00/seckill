package com.zjr.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    private static final String SECKILL_QUEUE = "seckill_queue";
    private static final String SECKILL_EXCHANGE = "seckill_exchange";

    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE);
    }

    @Bean
    public TopicExchange seckillTopicExchange() {
        return new TopicExchange(SECKILL_EXCHANGE);
    }

    @Bean
    public Binding seckillBinding() {
        return BindingBuilder.bind(seckillQueue())
                .to(seckillTopicExchange())
                .with("seckill.#");
    }


//
//    /**
//     * ******************************************************************
//     * 测试整合
//     */
//    @Bean
//    public Queue queue() {
//        return new Queue("queue", true);
//    }
//
//    /**
//     * ******************************************************************
//     * 测试Fanout模式
//     */
//    private static final String QUEUE_01 = "queue_fanout01";
//    private static final String QUEUE_02 = "queue_fanout02";
//    private static final String EXCHANGE_FA = "fanout_exchange";
//
//    @Bean
//    public Queue queue01() {
//        return new Queue(QUEUE_01);
//    }
//
//    @Bean
//    public Queue queue02() {
//        return new Queue(QUEUE_02);
//    }
//
//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(EXCHANGE_FA);
//    }
//
//    /**
//     * 将队列绑定到交换机内
//     */
//    @Bean
//    public Binding binding01() {
//        return BindingBuilder.bind(queue01()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding binding02() {
//        return BindingBuilder.bind(queue02()).to(fanoutExchange());
//    }
//
//    /**
//     * ******************************************************************
//     * 测试direct模式
//     */
//    private static final String QUEUE_DI_01 = "queue_direct01";
//    private static final String QUEUE_DI_02 = "queue_direct02";
//    private static final String EXCHANGE_DI = "direct_exchange";
//    private static final String ROUTING_KEY_DI_01 = "queue.red";
//    private static final String ROUTING_KEY_DI_02 = "queue.green";
//
//    @Bean
//    public Queue queueDi01() {
//        return new Queue(QUEUE_DI_01);
//    }
//
//    @Bean
//    public Queue queueDi02() {
//        return new Queue(QUEUE_DI_02);
//    }
//
//    @Bean
//    public DirectExchange directExchange() {
//        return new DirectExchange(EXCHANGE_DI);
//    }
//
//    @Bean
//    public Binding bindingDi01() {
//        return BindingBuilder.bind(queueDi01()).to(directExchange()).with(ROUTING_KEY_DI_01);
//    }
//
//    @Bean
//    public Binding bindingDi02() {
//        return BindingBuilder.bind(queueDi02()).to(directExchange()).with(ROUTING_KEY_DI_02);
//    }
//
//    /**
//     * ******************************************************************
//     * 测试topic模式
//     */
//    private static final String QUEUE_TO_01 = "queue_topic01";
//    private static final String QUEUE_TO_02 = "queue_topic02";
//    private static final String EXCHANGE_TO = "topic_exchange";
//    private static final String ROUTING_KEY_TO_01 = "#.queue.#";
//    private static final String ROUTING_KEY_TO_02 = "*.queue.#";
//
//    @Bean
//    public Queue queueTo01() {
//        return new Queue(QUEUE_TO_01);
//    }
//
//    @Bean
//    public Queue queueTo02() {
//        return new Queue(QUEUE_TO_02);
//    }
//
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange(EXCHANGE_TO);
//    }
//
//    @Bean
//    public Binding bindingTo01() {
//        return BindingBuilder.bind(queueTo01()).to(topicExchange()).with(ROUTING_KEY_TO_01);
//    }
//
//    @Bean
//    public Binding bindingTo02() {
//        return BindingBuilder.bind(queueTo02()).to(topicExchange()).with(ROUTING_KEY_TO_02);
//    }
}
