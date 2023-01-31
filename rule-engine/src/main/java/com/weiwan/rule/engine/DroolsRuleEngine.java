package com.weiwan.rule.engine;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.pojo.Rule;
import com.weiwan.rule.pojo.RuleSet;
import com.weiwan.rule.storage.RuleStorage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:01
 * @Package: com.weiwan.drools.rule
 * @ClassName: DroolsRuleEngine
 * @Description:DroolsRuleEngine
 **/
public class DroolsRuleEngine extends AbstractRuleEngine {

    private static final Logger logger = LoggerFactory.getLogger(DroolsRuleEngine.class);

    private final ConcurrentMap<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();

    @Override
    public void open(Map<String, Object> config) {
        RuleStorage storage = getRuleStorage();
        if (kieContainerMap.size() > 0) {
            throw new IllegalStateException("DroolsRuleEngine已经初始化, 无法再次初始化");
        }
        logger.info("开始进行 Drools Rule Engine 初始化工作.");
        createOrUpdate(storage);
        logger.info("Drools Rule Engine 初始化工作完成.");
    }

    private void createOrUpdate(RuleStorage storage) {
        Map<String, RuleSet> ruleSets = storage.getRuleSet();
        if (ruleSets != null && ruleSets.size() > 0) {
            //更新每个规则集
            for (Map.Entry<String, RuleSet> entry : ruleSets.entrySet()) {
                String key = entry.getKey();
                RuleSet ruleSet = entry.getValue();
                //生成kieContainer
                KieContainer kieContainer = buildKieContainer(ruleSet);
                //更新kieContainer, 这里执行后规则就生效了
                KieContainer oldContainer = kieContainerMap.put(key, kieContainer);
                if (oldContainer != null) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //这里手动销毁掉
                    oldContainer.dispose();
                }
                logger.info("完成: {}", key);
            }
        }
    }


    @Override
    public void stop() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String kieContainerKey : kieContainerMap.keySet()) {
            KieContainer kieContainer = kieContainerMap.get(kieContainerKey);
            //销毁每一个kieContainer
            kieContainer.dispose();
        }
    }

    @Override
    public void refresh(RuleStorage storage) {
        logger.info("==================== 更新 Drools Kie Containers ====================");
        logger.info("更新前Containers数量: {}", kieContainerMap.size());
        createOrUpdate(storage);
        logger.info("更新后Containers数量: {}", kieContainerMap.size());
        logger.info("==================== 更新 Drools Kie Containers 完成 ====================");
    }

    private static KieContainer buildKieContainer(RuleSet ruleSet) {
        KieServices kieServices = KieServices.get();
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(ruleSet.getRuleSetKey());
        kieBaseModel.newKieSessionModel(ruleSet.getRuleSetKey())
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("pseudo"))
                .setDefault(true);

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        List<Rule> rules = ruleSet.getRules();

        for (Rule rule : rules) {
            String fullPath = String.format("src/main/resources/ruleset/" + ruleSet.getTopic() + "/rule_%s.drl", rule.getRuleId());
            kieFileSystem.write(fullPath, rule.getRuleContent());
            kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            logger.info("rule error:{}", results.getMessages());
            throw new IllegalStateException("rule error");
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        return kieContainer;
    }

    @Override
    public void exec(JSONObject obj) {
        for (String key : kieContainerMap.keySet()) {
            KieContainer kieContainer = kieContainerMap.get(key);
            KieSession kieSession = kieContainer.newKieSession();
            //事实对象存入kie内存区域
            kieSession.insert(obj);
            //执行所有规则
            kieSession.fireAllRules();
            //销毁kieSession
            kieSession.dispose();
        }
    }
}
