# 大数据规则引擎封装



## 简介🎄



​	这是一个对规则引擎的封装, 为了方便的在如Flink、Spark、Java后台中集成规则引擎，将规则引擎做了一些简单的封装，将如规则加载，规则自动更新等基本功能抽象。目前实现了规则加载（Redis），自动重新装载(基于zk)，规则的简单执行等功能。

​	规则引擎分为两级结构，规则集和规则，这里比较宽泛，如在Drools中，我们动态的装载的一个drl文件就是一个规则，在Groovy中，我们一个脚本就是一个规则，数据结构默认采用的是``JSONObject``,如果需要使用自定义的对象，需要自己修改下代码。

> 注意，这个项目只适合轻度的使用规则引擎，如规则引擎的一些高级用法，实际上是需要自己来拓展的。💥

## 快速开始🚀



- 导入依赖

  ```xml
  <dependency>
      <groupId>com.weiwan</groupId>
      <artifactId>rule-engine</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```

  

- 构建引擎

  ```java
  Map<String,Object> configs = getRuleEngineConfig()        
  RuleEngine engine = RuleEngineFactory.builder()
      .type(EngineType.DROOLS)
      .config(configs)
      .init(true)
      .build(); //构建
  ```

  

- 配置

  ```
  Map<String, Object> config = new HashMap<>();
  //zookeeper地址, 用来监听
  config.put(RuleEngineConfig.WATCH_ZOOKEEPER_CONNECT, "127.0.0.1:2181");
  config.put(RuleEngineConfig.WATCH_ZOOKEEPER_PATH, "/rule_engine/change");
  //redis地址
  config.put(RuleEngineConfig.STORAGE_REDIS_HOST, "127.0.0.1");
  config.put(RuleEngineConfig.STORAGE_REDIS_PORT, 6379);
  config.put(RuleEngineConfig.STORAGE_REDIS_USER, "root");
  config.put(RuleEngineConfig.STORAGE_REDIS_PASSWD, "root123");
  //redis中规则的key的前缀
  config.put(RuleEngineConfig.STORAGE_REDIS_RULES_PREFIX, "rules.engine.set.*");
  ```

  

- 执行

  ```java
  engine.start(); //启动引擎
  engine.execute(jsonObject); //数据插入到规则引擎中
  ```



## 数据结构✨

- RuleSet

  ```java
  private String topic; //规则主题
  private String ruleSetKey; //规则集的key
  private Integer ruleSetCount; //规则集包含的规则数量
  private List<Rule> rules; //包含的所有规则
  ```

- Rule

  ```java
  private String ruleName; //规则名称
  private String ruleId; //规则ID
  private String ruleContent; //规则内容，这个实际上就是规则文件的具体内容
  private String expirationTime; //过期时间，暂无意义
  private String effectiveTime; //生效时间，暂无意义
  private Boolean base64; //规则内容是否base64
  ```

  

## Drools🎈



- 执行过程

  由于Drools是目前市面上用的较多的一种规则引擎，所以我优先实现了Drools，具体的规则执行代码如下

  ```java
  KieSession kieSession = kieContainer.newKieSession();
  //事实对象存入kie内存区域
  kieSession.insert(obj);
  //执行所有规则
  kieSession.fireAllRules();
  //销毁kieSession
  kieSession.dispose();
  ```

- 规则代码

  ```groovy
  package rules.engine.set.ruleset1
  import com.alibaba.fastjson.JSONObject
  
  rule "r_text_exist_1"
  when
      $fact:JSONObject(getString("text") != null)
  then
      $fact.put("textExist",1);
      System.out.println("触发了text存在检测规则, 添加存在检测标识");
  end
  ```



## 扩展&开发🧩

- RuleEngine&AbstractRuleEngine

  这个接口作为引擎的核心接口，可以通过继承``AbstractRuleEngine``来扩展规则引擎的具体实现，目前我简单实现Drools的相关功能。

- RuleStorage

  引擎规则的存储接口，定义了规则加载和自动重载的相关方法。RuleStorage需要传入一个RuleLoader和一个ReloadWatcher(开启自动重载)

- ReloadWatcher

  如果需要引擎有自动重新载入规则的能力，需要在构建RuleStorage时传入一个Watcher, 可以通过该接口自定义Watcher

- RuleLoader

  规则加载器，目前只实现了从Redis加载规则，如果需要实现其它规则加载方式，可以通过该接口来完成。



## 其它🔔

- 计划

  1. 更多的规则引擎实现）
  2. 更多的规则加载方式
  3. 更易用，配置化
  4. Flink算子整合
  5. SpringBoot-Starter整合等
  6. 。。。。。。。

- 联系我

  xiaozhennan1995@gmail.com