package com.weiwan.rule.pojo;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:43
 * @Package: com.weiwan.rule.pojo
 * @ClassName: RuleSet
 * @Description: 规则主题
 * 一个规则集中包含多个规则文件
 * 每个规则文件中有最少一个 rule
 *  * 一个{@link RuleSet} 对应redis中 一个 rule.engine.set.*
 * 一个{@link Rule} 对应redis中rule.engine.set.* 这个map中的一个key,
 * Rule中的ruleContent属性, 对应一个drl文件,一个drl文件中可以有多个drools的rule
 **/
public class RuleSet {
    private String topic;
    private String ruleSetKey;
    private Integer ruleSetCount;
    private List<Rule> rules;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getRuleSetKey() {
        return ruleSetKey;
    }

    public void setRuleSetKey(String ruleSetKey) {
        this.ruleSetKey = ruleSetKey;
    }

    public Integer getRuleSetCount() {
        return ruleSetCount;
    }

    public void setRuleSetCount(Integer ruleSetCount) {
        this.ruleSetCount = ruleSetCount;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
