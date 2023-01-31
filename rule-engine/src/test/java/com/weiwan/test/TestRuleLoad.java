package com.weiwan.test;

import com.weiwan.rule.engine.GroovyRuleEngine;
import com.weiwan.rule.engine.RuleEngine;

import java.util.HashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 17:38
 * @Package: com.weiwan.test
 * @ClassName: TestRuleLoad
 * @Description:
 **/
public class TestRuleLoad {
    public static void main(String[] args) {
        RuleEngine engine = new GroovyRuleEngine();
        HashMap<String, Object> config = new HashMap<>();
        config.put("rule.watch.zookeeper.connect", "127.0.0.1:2181");
        config.put("rule.watch.zookeeper.path", "/rule_engine/change");
        engine.init(config);
        while (true) {
            System.out.println("主线程休息");
            try {
                Thread.sleep(999999999999999999L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
