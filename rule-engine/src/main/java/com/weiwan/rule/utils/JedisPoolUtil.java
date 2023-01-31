//package com.weiwan.rule.utils;
//
//
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//
///**
// * @author: SunDC
// * @Date: 2020/9/8 3:19 下午
// * @Description: redis操作类
// * 采用懒汉式单例模式进行编写 线程安全且节约内存
// * 带有工厂的思想
// * <p>
// * 回收操作可以做在统一环绕通知那里
// */
//public class JedisPoolUtil {
//
//    // 被volatile修饰的变量不会被本地线程缓存，对该变量的读写都是直接操作共享内存。
//    private static volatile JedisPool jedisPool = null;
//
//    private JedisPoolUtil() {
//    }
//
//    public synchronized static JedisPool getInstance() {
//        if (null == jedisPool) {
//            synchronized (JedisPoolUtil.class) {
//                if (null == jedisPool) {
//                    JedisPoolConfig poolConfig = new JedisPoolConfig();
//                    //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
//                    poolConfig.setBlockWhenExhausted(true);
//                    //设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
//                    poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
//                    //是否启用pool的jmx管理功能, 默认true
//                    poolConfig.setJmxEnabled(true);
//                    //MBean ObjectName = new ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" + "pool" + i); 默 认为"pool", JMX不熟,具体不知道是干啥的...默认就好.
//                    poolConfig.setJmxNamePrefix("pool");
//                    //是否启用后进先出, 默认true
//                    poolConfig.setLifo(true);
//                    //最大空闲连接数, 默认8个
//                    poolConfig.setMaxIdle(8);
//                    //最大连接数, 默认8个
//                    poolConfig.setMaxTotal(8);
//                    //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
//                    poolConfig.setMaxWaitMillis(-1);
//                    //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
//                    poolConfig.setMinEvictableIdleTimeMillis(1800000);
//                    //最小空闲连接数, 默认0
//                    poolConfig.setMinIdle(0);
//                    //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
//                    poolConfig.setNumTestsPerEvictionRun(3);
//                    //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
//                    poolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
//                    //在获取连接的时候检查有效性, 默认false
//                    poolConfig.setTestOnBorrow(false);
//                    //在空闲时检查有效性, 默认false
//                    poolConfig.setTestWhileIdle(false);
//                    //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
//                    poolConfig.setTimeBetweenEvictionRunsMillis(-1);
//                    jedisPool = new JedisPool(poolConfig, "r-2ze8uv05uw7q54fe75pd.redis.rds.aliyuncs.com", 6379, "weiwan", "weiwan=123");
//                }
//            }
//        }
//        return jedisPool;
//    }
//
//    public static void clearResource(final Jedis jedis) {
//        //方法参数被声明为final，表示它是只读的。
//        if (jedis != null) {
//            jedis.close();
//        }
//    }
//
//
//}
//
//
