package com.weiwan.rule.storage;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.rule.common.RuleEngineConfig;
import com.weiwan.rule.engine.AbstractRuleEngine;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 15:36
 * @Package: com.weiwan.rule.storage
 * @ClassName: ZookeeperReloadWatcher
 * @Description:
 **/
public class ZookeeperReloadWatcher extends ReloadWatcher {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperReloadWatcher.class);
    private RuleStorage storage;
    private Map<String, Object> config;
    private String connectString;
    private String watchPath;
    private CuratorFramework client;

    @Override
    public void watch(RuleStorage storage, Map<String, Object> config, AbstractRuleEngine engine) {
        logger.info("规则变化监控-线程启动,开始监控规则变化...");
        this.storage = storage;
        this.config = config;
        this.connectString = (String) config.getOrDefault(RuleEngineConfig.WATCH_ZOOKEEPER_CONNECT, "127.0.0.0:2181");
        this.watchPath = (String) config.getOrDefault(RuleEngineConfig.WATCH_ZOOKEEPER_PATH, "/rule_engine/change");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(5000, 10);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .build();
        client.start();

        final NodeCache cache = new NodeCache(client, watchPath, false);
        try {
            cache.start(true);
            cache.getListenable().addListener(() -> {
                ChildData currentData = cache.getCurrentData();
                logger.info("规则变化监控-接收到规则变化事件通知");
                String data = currentData == null ? null : new String(currentData.getData());
                if (data != null) {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    long updateTime = jsonObject.getLongValue("update_time");
                    logger.info("规则变化监控-新的规则更新时间: {}", updateTime);
                    //storage reload之后 需要通知到engine重新生成
                    logger.info("规则变化监控-RuleStorage规则库重新载入中...");
                    boolean reload = storage.reload(updateTime);
                    logger.info("规则变化监控-RuleStorage规则库重新载入完成.");
                    if (reload) {
                        //reload之后, 通知引擎刷新引擎内的规则
                        try {
                            logger.info("规则变化监控-RuleEngine核心模块重新载入中...");
                            engine.refresh(storage);
                            logger.info("规则变化监控-RuleEngine核心模块重新载入完成.");
                        } catch (Exception e) {
                            logger.info("规则变化监控-RuleStorage规则库重新载入失败.", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
