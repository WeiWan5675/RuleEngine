package com.weiwan.rule.storage;

import com.weiwan.rule.pojo.RuleSet;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:41
 * @Package: com.weiwan.rule.storage
 * @ClassName: RuleStorage
 * @Description: 规则库
 **/
public interface RuleStorage {


    public void load();

    public boolean reload(long timestamp);

    public void clean();

    public Map<String, RuleSet> getRuleSet();

    void addWatcher(ReloadWatcher reloadWatcher);

    void enableWatch(boolean enable);
}
