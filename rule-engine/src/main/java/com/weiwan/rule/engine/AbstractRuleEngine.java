package com.weiwan.rule.engine;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.common.EngineType;
import com.weiwan.rule.common.RuleEngineConfig;
import com.weiwan.rule.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:13
 * @Package: com.weiwan.drools.rule
 * @ClassName: AbstractRuleEngine
 * @Description:
 **/
public abstract class AbstractRuleEngine implements RuleEngine {

    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractRuleEngine.class);
    private RuleStorage ruleStorage;
    private volatile boolean running;
    private volatile boolean inited;

    private Map<String, Object> config;

    private EngineType engineType;

    AbstractRuleEngine() {
    }

    void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    protected abstract void open(Map<String, Object> config);

    protected abstract void stop();

    protected abstract void exec(JSONObject obj);

    public abstract void refresh(RuleStorage ruleStorage);

    @Override
    public void close() {
        this.running = false;
        stop();
    }

    protected RuleStorage getRuleStorage() {
        return this.ruleStorage;
    }

    @Override
    public void init(final Map<String, Object> config) {
        if (inited) return;
        if (config == null) throw new NullPointerException("无法初始化规则引擎, 参数为空");
        setBasicEnv(config);
        //规则加载器
        RuleLoader ruleLoader = new RedisRuleLoader(config);
        //创建规则存储
        this.ruleStorage = new GenericRuleStorage(ruleLoader);
        //是否开启自动刷新
        ReloadWatcher reloadWatcher = new ZookeeperReloadWatcher();
        reloadWatcher.setConfig(config);
        reloadWatcher.setStorage(ruleStorage);
        reloadWatcher.setEngine(this);
        this.ruleStorage.addWatcher(reloadWatcher);
        this.inited = true;
    }

    @Override
    public void init() {
        if (config != null) {
            init(config);
        } else {
            throw new NullPointerException("无法初始化规则引擎, 参数为空");
        }
    }

    @Override
    public void start() {
        if (!inited) {
            init();
        }
        //第一次加载
        this.ruleStorage.load();
        //开启监听
        this.ruleStorage.enableWatch(true);
        //开启规则引擎
        open(config);
        //运行标识修改
        this.running = true;
    }

    private void setBasicEnv(Map<String, Object> config) {
        String dateFormat = (String) config.getOrDefault("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty(RuleEngineConfig.DROOLS_DATE_FORMAT, dateFormat);
    }


    @Override
    public void execute(JSONObject obj) {
        if (obj != null && this.running) {
            try {
                exec(obj);
            } catch (Exception e) {
                _LOGGER.error("规则引擎执行错误, 错误数据: {}", obj.toJSONString());
            }
            //规则引擎处理成功, 需要标识出来
            obj.put("rule_complete", true);
        }
    }
}
