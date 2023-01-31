package com.weiwan.rule.storage;

import com.weiwan.rule.pojo.RuleSet;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:52
 * @Package: com.weiwan.rule.storage
 * @ClassName: RuleLoader
 * @Description: 规则加载器
 **/
@FunctionalInterface
public interface RuleLoader {

    Map<String, RuleSet> load();

}
