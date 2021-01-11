package com.oauth.jwt.common.core.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Log4j2
@Component
public class RedisUtil {

    private static RedisTemplate<String, Object> redisTemplate;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 工具类的注入
     */
    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        log.debug("redisTemplate加载");
        RedisUtil.redisTemplate = redisTemplate;
    }

    /**
     * 指定缓存时效时间
     *
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.MINUTES);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取key过期时间
     *
     * @return 返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MINUTES);
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断key是否存在，异常:{}", e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 普通获取
     */
    public Object get(String key) {
        try {
            return null == key ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 普通放入
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通放入并设置时间
     *
     * @param time 时间(秒)
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

