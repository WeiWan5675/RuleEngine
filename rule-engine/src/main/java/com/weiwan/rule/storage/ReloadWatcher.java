package com.weiwan.rule.storage;

import com.weiwan.rule.engine.AbstractRuleEngine;

import java.util.Map;

public abstract class ReloadWatcher extends Thread {
    private RuleStorage storage;
    private Map<String, Object> config;
    protected AbstractRuleEngine engine;

    public void setStorage(RuleStorage storage) {
        this.storage = storage;
    }
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public ReloadWatcher(RuleStorage storage, Map<String, Object> config) {
        this();
        this.storage = storage;
        this.config = config;
    }

    public ReloadWatcher() {
        this.setDaemon(true);
    }


    @Override
    public void run() {
        /**
         * 这里根据不同的watcher, 实现方式不同
         * zk的是通过添加监听器来实现, 也可以通过实现如轮询的方式来实现
         */
        watch(this.storage, config, engine);
    }

    public abstract void watch(RuleStorage storage, Map<String, Object> config, AbstractRuleEngine engine);

    public void setEngine(AbstractRuleEngine engine) {
        this.engine = engine;
    }

}