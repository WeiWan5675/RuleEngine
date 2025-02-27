package com.weiwan.rule.engine;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 13:59
 * @Package: com.weiwan.drools.rule
 * @ClassName: RuleEngine
 * @Description: 规则引擎
 **/
public interface RuleEngine {

    void init(Map<String, Object> config);
    void init();
    void start();

    void close();

    void execute(JSONObject obj);

    void execute(String topic, JSONObject obj);

}
