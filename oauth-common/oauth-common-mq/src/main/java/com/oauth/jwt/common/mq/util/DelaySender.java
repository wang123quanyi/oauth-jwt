package com.oauth.jwt.common.mq.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DelaySender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendDelay(String order) {
        log.info("【订单生成时间】" + new Date().toString() + "【1分钟后检查订单是否已经支付】" + order);
        this.amqpTemplate.convertAndSend(DelayRabbitConfig.ORDER_DELAY_EXCHANGE, DelayRabbitConfig.ORDER_DELAY_ROUTING_KEY, order, new MessagePostProcessor() {
            public Message postProcessMessage(Message message) throws AmqpException {
                // 如果配置了 params.put("x-message-ttl", 5 * 1000); 那么这一句也可以省略,具体根据业务需要是声明 Queue 的时候就指定好延迟时间还是在发送自己控制时间
                message.getMessageProperties().setExpiration(1 * 1000 * 60 + "");
                return message;
            }
        });
    }

}

