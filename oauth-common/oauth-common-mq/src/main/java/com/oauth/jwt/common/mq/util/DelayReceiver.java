package com.oauth.jwt.common.mq.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DelayReceiver {
    @RabbitListener(queues = {DelayRabbitConfig.ORDER_QUEUE_NAME})
    public void orderDelayQueue(String order) {
        log.info("###########################################");
        log.info("【orderDelayQueue 监听的消息】 - 【消费时间】 - [{}]- 【订单内容】 - [{}]", new Date(), order);
        log.info("###########################################");
    }

}

