package com.weiwan.test;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.common.EngineType;
import com.weiwan.rule.common.RuleEngineConfig;
import com.weiwan.rule.engine.DroolsRuleEngine;
import com.weiwan.rule.engine.RuleEngine;
import com.weiwan.rule.engine.RuleEngineFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/30 10:07
 * @Package: com.weiwan.test
 * @ClassName: TestDroolsRuleEngine
 * @Description:
 **/
public class TestDroolsRuleEngine {
    public static void main(String[] args) {
        //设置打印日志
        loggerSetting();

        //构建引擎
        RuleEngine engine = RuleEngineFactory.builder()
                .type(EngineType.DROOLS)
                .config(getRuleEngineConfig())
                .init(true)
                .build(); //构建


        engine.start();

        //测试数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", "aaaaaaaa");

        //规则执行
        int index = 0;
        long startTime = System.currentTimeMillis();
        while (index < 9999999) {
            engine.execute(jsonObject);
            System.out.println("执行结果: " + jsonObject.toJSONString());
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            index++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("执行总耗时: " + (endTime - startTime) + " s");
    }

    private static Map<String, Object> getRuleEngineConfig() {
        Map<String, Object> config = new HashMap<>();
        //zookeeper地址, 用来监听
        config.put(RuleEngineConfig.WATCH_ZOOKEEPER_CONNECT, "127.0.0.1:2181");
        config.put(RuleEngineConfig.WATCH_ZOOKEEPER_PATH, "/rule_engine/change");

        //redis地址
        config.put(RuleEngineConfig.STORAGE_REDIS_HOST, "r-2ze8uv05uw7q54fe75pd.redis.rds.aliyuncs.com");
        config.put(RuleEngineConfig.STORAGE_REDIS_PORT, 6379);
        config.put(RuleEngineConfig.STORAGE_REDIS_USER, "weiwan");
        config.put(RuleEngineConfig.STORAGE_REDIS_PASSWD, "weiwan=123");

        //redis中规则的key的前缀
        config.put(RuleEngineConfig.STORAGE_REDIS_RULES_PREFIX, "rules.engine.set.*");
        return config;
    }

    private static void loggerSetting() {
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.INFO);
        root.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %-60c %x - %m%n")));
    }
}
