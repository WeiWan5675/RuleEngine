package com.weiwan.rule.utils;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

public class RedisHelper {

    private final Integer redisPort;
    private final String redisHost;
    private final String redisPassword;
    private final String redisUser;

    public RedisHelper(String redisHost, Integer redisPort, String redisPassword, String redisUser) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
        this.redisPassword = redisPassword;
        this.redisUser = redisUser;
    }


    /**
     * 通过key获取所有的field和value
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            if (StringUtils.isNotBlank(redisUser) && StringUtils.isNotBlank(redisPassword)) {
                jedis.auth(redisUser, redisPassword);
            }
            return jedis.hgetAll(key);
        }
    }

    /**
     * 返回满足pattern表达式的所有key
     * keys(*)
     * 返回所有的key
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            if (StringUtils.isNotBlank(redisUser) && StringUtils.isNotBlank(redisPassword)) {
                jedis.auth(redisUser, redisPassword);
            }
            return jedis.keys(pattern);
        }
    }

}


