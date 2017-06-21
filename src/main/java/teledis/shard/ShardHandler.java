package teledis.shard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import teledis.client.impl.CallBack;
import teledis.config.CfgManager;
import teledis.config.ShardInfo4Jedis;
import teledis.exception.RedisClientException;

/**
 * 处理一个分片中的所有操作<br>
 * 
 * @author 
 */
public class ShardHandler {
    /**
     * 日志记录
     */
    private static Logger logger = LoggerFactory.getLogger(ShardHandler.class);
    /**
     * 标记同一shard另一节点是否可用的key
     */
    private static final String SNFREDISDISABLEDSIGN = "__SNF__REDIS__DISABLED__SIGN__";
    /**
     * 该shard全部节点的pool，包含不可用的pool
     */
    private List<TelekJedisPool> pools = new ArrayList<TelekJedisPool>();
    /**
     * 该shard不可用的pool
     */
    private volatile Set<TelekJedisPool> errorPools = new HashSet<TelekJedisPool>();
    /**
     * shard的配置参数
     */
    private ShardInfo4Jedis shardInfo4Jedis;
    /**
     * 是否是master-master模式
     */
    private boolean isMM = false;

    /**
     * 线程执行器
     */
    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 构造方法
     * 
     * @param pools
     * @param shardInfo4Jedis
     */
    public ShardHandler(List<TelekJedisPool> pools, ShardInfo4Jedis shardInfo4Jedis) {
        super();
        if (pools != null) {
            this.pools.addAll(pools);
        }
        if (this.pools.size() == 2) {
            isMM = true;
        }
        this.shardInfo4Jedis = shardInfo4Jedis;
        getError();
        sync();
    }

    private synchronized void getError() {
        if (isMM) {
            int size = pools.size();
            if (errorPools.size() == 2) {
                // 重置
                clearDisableFlags();
            }
            for (int i = 0; i < size; i++) {
                TelekJedisPool pool = pools.get(i);
                try {
                    if (!errorPools.contains(pool) && !isAvailable(pool)) {
                        errorPools.add(pool);
                    } else if (errorPools.contains(pool) && isAvailable(pool)) {
                        errorPools.remove(pool);
                    }
                } catch (RedisClientException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    private void sync() {
        if (isMM) {
            this.scheduledExecutor.schedule(new Runnable() {
                public void run() {
                    getError();
                    sync();
                }
            }, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 
     * toString
     */
    @Override
    public String toString() {
        return "ShardHandler [shardInfo4Jedis=" + shardInfo4Jedis + "]";
    }

    /**
     * 
     * 功能描述 <br>
     * 是否可用
     * 
     * @param 参数说明  
     * @return 
     * @throw 异常描述
     * @see 
     */
    private boolean isAvailable(TelekJedisPool errorPool) {
        int recorder = pools.indexOf(errorPool) == 0 ? 1 : 0;
        TelekJedisPool recorderPool = pools.get(recorder);
        return !invoke(recorderPool, new CallBack<Boolean>() {
            @Override
            public Boolean invoke(Jedis jedis) {
                return jedis.exists(SNFREDISDISABLEDSIGN);
            }
        });
    }

    /**
     * 
     * 功能描述�?br>
     * 随机返回可用的pool
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private TelekJedisPool getPool() {
        TelekJedisPool returnPool = null;
        if (isMM) {
            if (errorPools.isEmpty()) {
                int poolTag = new Random().nextInt(2);
                // 选中的pool
                TelekJedisPool pool = pools.get(poolTag);
                if (errorPools.contains(pool)) {
                    returnPool = pools.get(poolTag == 0 ? 1 : 0);
                } else {
                    returnPool = pool;
                }
            } else {
                returnPool = errorPools.contains(pools.get(0)) ? pools.get(1) : pools.get(0);
            }
        } else {
            returnPool = pools.get(0);
        }
        return returnPool;
    }

    /**
     * 功能描述：标记不可用，并加入不可用列�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private void onError(final TelekJedisPool errorPool) {
        if (isMM) {
            synchronized (this) {
                if (errorPools.isEmpty()) {
                    int recorder = pools.indexOf(errorPool) == 0 ? 1 : 0;
                    TelekJedisPool recorderPool = pools.get(recorder);
                    try {
                        invoke(recorderPool, new CallBack<String>() {
                            @Override
                            public String invoke(Jedis jedis) {
                                return jedis.set(SNFREDISDISABLEDSIGN, errorPool.toString());
                            }
                        });
                    } catch (RedisClientException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 
     * 功能描述: <br>
     * 清除不可用标�?
     * 
     */
    public void clearDisableFlags() {
        for (TelekJedisPool jedisPool : pools) {
            try {
                invoke(jedisPool, new CallBack<Long>() {
                    @Override
                    public Long invoke(Jedis jedis) {
                        return jedis.del(SNFREDISDISABLEDSIGN);
                    }
                });
            } catch (RedisClientException e) {
                logger.error(e.getMessage());
            }
        }
        errorPools.clear();
    }

    /**
     * 
     * 功能描述: <br>
     * 清空shard中所有数�?
     * 
     */
    public void flushShard() {
        for (TelekJedisPool jedisPool : pools) {
            try {
                invoke(jedisPool, new CallBack<String>() {
                    @Override
                    public String invoke(Jedis jedis) {
                        return jedis.flushDB();
                    }
                });
            } catch (RedisClientException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 
     * 功能描述�?�?���?���?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public void destroy() {
        for (TelekJedisPool jedisPool : pools) {
            try {
                jedisPool.destroy();
            } catch (JedisException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 
     * 功能描述：invoke
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private <R> R invoke(TelekJedisPool jedisPool, CallBack<R> callBack) {
        boolean isClosed = false;
        Jedis jedis = null;
        try {
            // 获取连接
            jedis = jedisPool.getResource();
            return callBack.invoke(jedis);
        } catch (Exception e) {
            if (e instanceof JedisConnectionException) {
                if (jedis != null) {
                    try {
                        // 处理无效连接
                        jedisPool.returnBrokenResource(jedis);
                        isClosed = true;
                    } catch (JedisException e1) {
                        logger.error(e1.getMessage(),e1);
                    }
                }
            }
            // 抛出异常
            throw new RedisClientException(e);
        } finally {
            if (!isClosed) {
                if (jedis != null) {
                    try {
                        // 归还连接
                        jedisPool.returnResource(jedis);
                    } catch (JedisException e1) {
                        logger.error(e1.getMessage(),e1);
                    }
                }
            }
        }
    }

    /**
     * 
     * 功能描述�?br>
     * execute
     * 
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public <R> R execute(CallBack<R> callBack) {
        // 获取上一方法�?
        String preMethodName = new Exception().getStackTrace()[2].getMethodName();
        // 若是读方�?
        if (ReadMethodTags.isReadMethod(preMethodName)) {
            // 错误次数
            int eCount = 0;
            TelekJedisPool jedisPool = null;
            while (true) {
                jedisPool = getPool();
                try {
                    return invoke(jedisPool, callBack);
                } catch (RedisClientException e) {
                    // 错误次数�?
                    eCount++;
                    // 若错误数未到限制
                    if (eCount < CfgManager.RETRYTIMES) {
                        continue;
                    }
                    // 加入不可用列�?
                    onError(jedisPool);
                    // 抛出异常
                    throw e;
                }
            }
        } else {
            R r = null;
            // 错误的池的个�?
            int eCount = 0;
            // 抛出的异�?
            RedisClientException eThrow = null;
            List<TelekJedisPool> pools = getAllAvailablePools();
            // 池的个数
            int poolsSize = pools.size();
            // 循环�?
            for (TelekJedisPool jedisPool : pools) {
                for (int i = 0; i < CfgManager.RETRYTIMES; i++) {
                    try {
                        // 执行
                        r = invoke(jedisPool, callBack);
                    } catch (RedisClientException e) {
                        // 重试次数到限�?
                        if (i == CfgManager.RETRYTIMES - 1) {
                            // 错误的池的个数加1
                            eCount++;
                            // 抛出的异�?
                            eThrow = e;
                            // 加入不可用列�?
                            onError(jedisPool);
                        } else {
                            continue;
                        }
                    }
                    break;
                }
            }
            // �?��池抛异常�?告知调用�?
            if (eCount == poolsSize) {
                throw eThrow;
            }
            return r;
        }
    }

    /**
     * 
     * 功能描述：获取所�?可用�?连接�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private List<TelekJedisPool> getAllAvailablePools() {
        if (isMM) {
            List<TelekJedisPool> lst = new ArrayList<TelekJedisPool>();
            for (TelekJedisPool jedisPool : pools) {
                if (!errorPools.contains(jedisPool)) {
                    lst.add(jedisPool);
                }
            }
            return lst;
        } else {
            return pools;
        }
    }
}
