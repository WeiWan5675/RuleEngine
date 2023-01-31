package com.weiwan.rule.storage;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.common.RuleEngineConfig;
import com.weiwan.rule.pojo.Rule;
import com.weiwan.rule.pojo.RuleSet;
import com.weiwan.rule.utils.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 15:06
 * @Package: com.weiwan.rule.storage
 * @ClassName: RedisRuleLoader
 * @Description:
 **/
public class RedisRuleLoader implements RuleLoader {

    private static final Logger logger = LoggerFactory.getLogger(RedisRuleLoader.class);
    private Map<String, Object> config;
    private Integer redisPort;
    private String redisHost;
    private String redisPassword;
    private String redisUser;

    private String ruleKeyPrefix;

    public RedisRuleLoader(Map<String, Object> config) {
        this.config = config;
        this.redisHost = (String) config.getOrDefault(RuleEngineConfig.STORAGE_REDIS_HOST, "127.0.0.1");
        this.redisPort = (Integer) config.getOrDefault(RuleEngineConfig.STORAGE_REDIS_PORT, 6379);
        this.redisPassword = (String) config.getOrDefault(RuleEngineConfig.STORAGE_REDIS_PASSWD, "");
        this.redisUser = (String) config.getOrDefault(RuleEngineConfig.STORAGE_REDIS_USER, "");
        this.ruleKeyPrefix = (String) config.getOrDefault(RuleEngineConfig.STORAGE_REDIS_RULES_PREFIX, "rules.engine.set.*");
    }

    @Override
    public Map<String, RuleSet> load() {
        logger.info("==================== 开始从Redis加载规则 ====================");
        //redis操作工具类
        RedisHelper redisHelper = new RedisHelper(redisHost, redisPort, redisPassword, redisUser);
        //获取所有规则引擎的规则
        Set<String> keys = redisHelper.keys(ruleKeyPrefix);
        //定义新的规则存储集合
        Map<String, RuleSet> ruleSetMap = new ConcurrentHashMap<>();
        //处理所有规则
        for (String setKey : keys) {
            Map<String, String> ruleSetAll = redisHelper.hgetAll(setKey);
            RuleSet ruleSet = new RuleSet();
            String topic = setKey.substring(setKey.lastIndexOf(".") + 1);
            ruleSet.setTopic(topic);
            ruleSet.setRuleSetKey(setKey);
            ruleSet.setRuleSetCount(ruleSetAll.size());
            List<Rule> rules = new ArrayList<Rule>();
            ruleSet.setRules(rules);
            for (Map.Entry<String, String> entry : ruleSetAll.entrySet()) {
                String ruleJson = entry.getValue();
                Rule rule = JSONObject.parseObject(ruleJson, Rule.class);
                //规则文件经过base64
                if (rule.isBase64()) {
                    String ruleContent = rule.getRuleContent();
                    try {
                        byte[] decode = Base64.getDecoder().decode(ruleContent.getBytes(RuleEngineConfig.RULE_CHARSET_UTF8));
                        rule.setRuleContent(new String(decode, RuleEngineConfig.RULE_CHARSET_UTF8));
                    } catch (UnsupportedEncodingException e) {
                        logger.error("规则解码出错, 规则ID: {}, 规则名称: {}", rule.getRuleId(), rule.getRuleName());
                        throw new RuntimeException(e);
                    }
                }
                rules.add(rule);
            }
            logger.info("加载: {}, 规则数量: {}", ruleSet.getRuleSetKey(), ruleSet.getRuleSetCount());
            ruleSetMap.put(setKey, ruleSet);
        }
        logger.info("==================== 从Redis加载规则结束 ====================");
        return ruleSetMap;
    }
}
