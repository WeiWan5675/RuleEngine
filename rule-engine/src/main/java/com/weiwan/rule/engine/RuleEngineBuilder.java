package com.weiwan.rule.engine;

import com.weiwan.rule.common.EngineType;

import java.util.Map;

public class RuleEngineBuilder {
    private Map<String, Object> config;
    private EngineType engineType;

    private boolean initialize;

    public RuleEngineBuilder config(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public RuleEngineBuilder type(EngineType engineType) {
        this.engineType = engineType;
        return this;
    }

    public RuleEngineBuilder init(boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    public RuleEngine build() {
        AbstractRuleEngine ruleEngine = (AbstractRuleEngine) RuleEngineFactory.createRuleEngine(engineType);
        ruleEngine.setEngineType(engineType);
        ruleEngine.setConfig(config);
        if(initialize){
            ruleEngine.init();
        }
        return ruleEngine;
    }
}