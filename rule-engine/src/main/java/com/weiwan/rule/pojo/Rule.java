package com.weiwan.rule.pojo;

/**
 * @Author: xiaozhennan
 * @Date: 2023/1/19 14:43
 * @Package: com.weiwan.rule.pojo
 * @ClassName: Rule
 * @Description: 规则
 **/
public class Rule {
    private String ruleName;
    private String ruleId;
    private String ruleContent;
    private String expirationTime;
    private String effectiveTime;
    private Boolean base64;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Boolean isBase64() {
        return base64;
    }

    public void setBase64(Boolean base64) {
        this.base64 = base64;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
}
