package com.weiwan.rule.common;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/30 14:06
 * @Package: com.weiwan.rule.common
 * @ClassName: RuleEngineConfig
 * @Description:
 **/
public class RuleEngineConfig {
    public static final String WATCH_ZOOKEEPER_CONNECT = "rule.watch.zookeeper.connect";
    public static final String WATCH_ZOOKEEPER_PATH = "rule.watch.zookeeper.path";

    public static final String STORAGE_REDIS_HOST = "rule.storage.redis.host";
    public static final String STORAGE_REDIS_PORT = "rule.storage.redis.port";
    public static final String STORAGE_REDIS_USER = "rule.storage.redis.user";
    public static final String STORAGE_REDIS_PASSWD = "rule.storage.redis.passwd";
    public static final String STORAGE_REDIS_RULES_PREFIX = "rules.engine.set.*";

    public static final String RULE_CHARSET_UTF8 = "utf8";

    public static final String DROOLS_DATE_FORMAT = "drools.dateformat";
}
