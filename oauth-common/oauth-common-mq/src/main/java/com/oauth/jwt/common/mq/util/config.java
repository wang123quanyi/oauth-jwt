package com.oauth.jwt.common.mq.util;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class config {
    public static final String QUEUE_NAME = "test.queue";
    public static final String EXCHANGE_NAME = "test.exchange";
    public static final String ROUTING_KEY = "test.key";
    public static String delayRoutingKey = "delay.key";
    public static String delayQueueBegin = "delay.queue.";
    public static String delayExchangeBegin = "delay.exchange.";
    public static String math;

    static {
        String strs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String str = strs.charAt((int) (Math.random() * 26)) + "" + strs.charAt((int) (Math.random() * 26));
        DateFormat format = new SimpleDateFormat("MMdd");
        math = format.format(new Date()) + str;
    }

    @Bean
    public Queue registerQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange registerTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding registerBinding() {
        return BindingBuilder.bind(registerQueue()).to(registerTopicExchange()).with(ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public Channel createChannel(ConnectionFactory connectionFactory) {
        return connectionFactory.createConnection().createChannel(true);
    }
}
