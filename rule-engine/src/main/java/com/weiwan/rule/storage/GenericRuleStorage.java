package com.weiwan.rule.storage;

import com.weiwan.rule.pojo.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:42
 * @Package: com.weiwan.rule.storage
 * @ClassName: GenericRuleStorage
 * @Description: 默认规则库
 **/
public class GenericRuleStorage implements RuleStorage {

    private static final Logger logger = LoggerFactory.getLogger(GenericRuleStorage.class);
    private Map<String, RuleSet> ruleTopics = new ConcurrentHashMap<>();
    private RuleLoader ruleLoader;
    private ReloadWatcher reloadWatcher;
    private volatile long updateTime = -1;
    private volatile boolean complete = false;
    private volatile boolean enableAutoReload = false;

    public GenericRuleStorage(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    @Override
    public synchronized void load() {
        //这里实现数据加载的逻辑
        if (!complete) {
            this.ruleTopics = ruleLoader.load();
            this.complete = true;
        }
    }

    @Override
    public synchronized boolean reload(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("开始进行规则库重新载入, 上次更新时间: {}", sdf.format(new Date(updateTime)));
        if (timestamp > updateTime) {
            //本次更新时间大于上次的
            Map<String, RuleSet> newRules = ruleLoader.load();
            if (newRules != null) {
                clean();
                this.ruleTopics = newRules;
                updateTime = timestamp;
                logger.info("规则库重新载入已完成, 本次更新时间: {}", sdf.format(new Date(updateTime)));
            }
            complete = true;
        }
        return complete;
    }


    @Override
    public synchronized void clean() {
        this.complete = false;
        this.ruleTopics = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Map<String, RuleSet> getRuleSet() {
        if (complete) {
            return this.ruleTopics;
        } else {
            return new ConcurrentHashMap<>();
        }
    }

    @Override
    public void addWatcher(ReloadWatcher watcher) {
        this.reloadWatcher = watcher;
    }

    @Override
    public void enableWatch(boolean enable) {
        if (this.reloadWatcher != null && enable && !this.enableAutoReload) {
            this.enableAutoReload = true;
            this.reloadWatcher.start();
        } else {
            //这里reload线程不需要具有关闭功能, 如果需要可以在这里扩展
            this.enableAutoReload = false;
        }
    }
}
