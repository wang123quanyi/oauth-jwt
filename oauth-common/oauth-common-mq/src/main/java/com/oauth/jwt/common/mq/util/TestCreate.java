package com.oauth.jwt.common.mq.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class TestCreate implements CommandLineRunner {

    private final Channel channel;
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static Map<Integer, Set<Integer>> touchHours = new HashMap<>();
    public static List<String> queues = new ArrayList<>();
    private static Map<String, Object> params;

    static {
        params = new HashMap<>();
        params.put("x-dead-letter-exchange", config.EXCHANGE_NAME);
        params.put("x-dead-letter-routing-key", config.ROUTING_KEY);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("\n{}", config.math);
        if (0 == touchHours.size()) createTouchHours(true);
        cleanQueue();
//        te();
    }


    @RabbitListener(queues = (config.QUEUE_NAME), containerFactory = "rabbitListenerContainerFactory")
    public void listener(@Payload String reqString) {
        System.out.println("\n得到触发节点监听的消息 - 消费时间:" + sdf.format(new Date()) + ",delay - reqString:" + reqString);
        try {
            if (0 == touchHours.size()) createTouchHours(false);
            JSONObject jsonObject = JSON.parseObject(reqString);
            Integer rulePage = jsonObject.getInteger("allocationRulePage");
            Integer stage = jsonObject.getInteger("stage");
            if (null == stage) stage = 0;
            stage = stage + 1;
            Set<Integer> integers = touchHours.get(rulePage);
            if (null != integers && 0 != integers.size() && integers.contains(stage)) {
                String exchange = config.delayExchangeBegin + stage + rulePage + "." + config.math;
                jsonObject.put("stage", stage);
                this.rabbitTemplate.convertAndSend(exchange, config.delayRoutingKey, jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Scheduled(cron = "0/20 * * * * ? ")
    public void te() throws Exception {
        Long l = 5556489674896L;
        Iterator<Map.Entry<Integer, Set<Integer>>> iterator = touchHours.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Set<Integer>> next = iterator.next();
            Integer rulePage = next.getKey();
            Iterator<Integer> iterator1 = next.getValue().iterator();
            while (iterator1.hasNext()) {
                String exchange = config.delayExchangeBegin + iterator1.next() + rulePage + "." + config.math;
                this.rabbitTemplate.convertAndSend(exchange, config.delayRoutingKey, "s");
                l++;
            }
        }
    }

    private void createTouchHours(boolean declare) {
        int millisDay = 1000 * 60 * 60 * 24;
        int millisHour = 1000 * 60 * 60;
        int millisMinute = 1000 * 60;
        Map<Integer, String> touchHour = new HashMap<>();
        Iterator<Map.Entry<Integer, String>> iterator = touchHour.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> next = iterator.next();
            Integer rulePage = next.getKey();
            JSONObject jsonObject = JSON.parseObject(next.getValue());
            List<Integer> integers = new ArrayList<>();
            String d = jsonObject.getString("D");
            if (StrUtil.isNotBlank(d)) {
                String[] split = d.split(",");
                for (int k = 0; k < split.length; k++) {
                    integers.add(Integer.valueOf(split[k]) * millisDay);
                }
            }
            String h = jsonObject.getString("H");
            if (StrUtil.isNotBlank(h)) {
                String[] split = h.split(",");
                for (int k = 0; k < split.length; k++) {
                    integers.add(Integer.valueOf(split[k]) * millisHour);
                }
            }
            String m = jsonObject.getString("M");
            if (StrUtil.isNotBlank(m)) {
                String[] split = m.split(",");
                for (int k = 0; k < split.length; k++) {
                    integers.add(Integer.valueOf(split[k]) * millisMinute);
                }
            }
            Collections.sort(integers);
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < integers.size(); i++) {
                set.add(i);
                if (declare) {
                    Integer integer = integers.get(i);
                    if (0 < i) {
                        integer = integer - integers.get(i - 1);
                    }
                    String key = i + "" + rulePage + "." + config.math;
                    queues.add(key);
                    String delayQueueName = config.delayQueueBegin + key;
                    params.put("x-message-ttl", integer);
                    try {
                        Queue queue = new Queue(delayQueueName, true, false, false, params);
                        rabbitAdmin.declareQueue(queue);
                        DirectExchange directExchange = new DirectExchange(config.delayExchangeBegin + key);
                        rabbitAdmin.declareExchange(directExchange);
                        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(config.delayRoutingKey));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            touchHours.put(rulePage, set);
        }
    }

    private void cleanQueue() {
        List<Object> queue1 = new ArrayList<>();
        try {
            for (int i = 0; i < queue1.size(); i++) {
                String key = (String) queue1.get(i);
                channel.queueDelete(config.delayQueueBegin + key, false, true);
                channel.exchangeDelete(config.delayExchangeBegin + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        queue1.addAll(queues);
    }
}