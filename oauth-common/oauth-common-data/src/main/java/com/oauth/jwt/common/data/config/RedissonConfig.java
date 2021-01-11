package com.oauth.jwt.common.data.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
public class RedissonConfig {
    @Resource
    private Environment env;

    @Bean
    public Redisson redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + getAddress() + ":6379").setPassword(getPassword()).setDatabase(1);
        return (Redisson) Redisson.create(config);
    }

    String getAddress() {
        String host = "192.168.1.181";//env.getProperty("spring.main.redis.host");
        return host;
    }

    String getPassword() {
        String password = "campo";//env.getProperty("spring.main.redis.password");
        return password;
    }
}
