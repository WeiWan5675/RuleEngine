package com.weiwan.rule.engine;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.pojo.RuleSet;
import com.weiwan.rule.storage.RuleStorage;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:11
 * @Package: com.weiwan.drools.rule
 * @ClassName: GroovyRuleEngine
 * @Description: GroovyRuleEngine
 **/
public class GroovyRuleEngine extends AbstractRuleEngine {

    @Override
    public void open(Map<String, Object> config) {
        System.out.println("config: " + config);
    }

    @Override
    public void stop() {

    }

    @Override
    protected void exec(JSONObject obj) {

    }

    @Override
    public void refresh(RuleStorage ruleStorage) {
        //key就是redis的key
//        Map<String, RuleSet> ruleSet = ruleStorage.getRuleSet();
        //value就是redis的map中所有的值
    }
}
