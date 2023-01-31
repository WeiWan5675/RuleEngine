package com.weiwan.rule.engine;

import com.weiwan.rule.common.EngineType;
import com.weiwan.rule.common.RuleEngineConfig;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/31 15:19
 * @Package: com.weiwan.rule.engine
 * @ClassName: RuleEngineFactory
 * @Description: 规则引擎工厂类
 **/
public class RuleEngineFactory {


    protected static RuleEngine createRuleEngine(EngineType engineType) {
        switch (engineType) {
            case DROOLS:
                return new DroolsRuleEngine();
            case GROOVY:
                return new GroovyRuleEngine();
            default:
                throw new IllegalStateException("未知的规则引擎类型, 无法创建.");
        }
    }

    private static RuleEngine createDroolsEngine() {
        return createRuleEngine(EngineType.DROOLS);
    }


    private static RuleEngine createGroovyEngine() {
        return createRuleEngine(EngineType.DROOLS);
    }

    public static final RuleEngineBuilder builder() {
        return new RuleEngineBuilder();
    }
}

