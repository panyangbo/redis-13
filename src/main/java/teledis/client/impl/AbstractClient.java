package teledis.client.impl;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teledis.config.CfgManager;
import teledis.shard.ShardHandler;
import teledis.shard.ShardPools4Jedis;

/**
 * 
 * 功能描述操作接口抽象
 * 
 * @author 
 * @version 1.0.0
 */
public abstract class AbstractClient{
    public static final String UNSUPPORT = "Current configuration does not support this operation";
    private static Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    protected ShardPools4Jedis shardPools4Jedis;

    private CfgManager cfgManager;
    
    public AbstractClient() {
        cfgManager = new CfgManager();
        this.init();
    }

    public AbstractClient(String config) {
        cfgManager = new CfgManager(config);
        this.init();
    }

    /**
     * 
     * 功能描述isSharding
     * 
     * @param 参数说明 返回 类型 <说明>
     * @return 返回
     * @throw 异常描述
     * @see 参见的其它内
     */
    protected boolean isSharding() {
        return cfgManager.isSharding();
    }

    protected boolean isHashing() {
		return cfgManager.isHashing();
	}
    /**
     * 
     * 功能描述：初始化
     * 
     * @param 参数说明 返回 类型 <说明>
     * @return 返回
     * @throw 异常描述
     * @see 参见的其它内
     */
    private synchronized void init() {
        // 加载配置
        cfgManager.loadConfig();
        shardPools4Jedis = new ShardPools4Jedis(cfgManager.getLstInfo4Jedis(),isHashing());
    }

    // 刷新配置
    public synchronized void refresh(String config) {
        ShardPools4Jedis old = shardPools4Jedis;
        if (!StringUtils.isBlank(config)) {
            cfgManager.setConfig(config);
        }
        this.init();
        // 旧池
        Collection<ShardHandler> allShards = old.getAllShards();
        for (ShardHandler shardHandler : allShards) {
            shardHandler.destroy();
        }
    }

    /**
     * 
     * 功能描述: <br>
     * 清空数据
     * 
     * @return
     */
    public String flushDB() {
        Collection<ShardHandler> allShards = shardPools4Jedis.getAllShards();
        final CountDownLatch endSignal = new CountDownLatch(allShards.size());
        for (final ShardHandler shard : allShards) {
            // 多线程同时flushDB 提高效率
            new Thread(new Runnable() {
                @Override
                public void run() {
                    shard.flushShard();
                    endSignal.countDown();
                }
            }).start();
        }
        try {
            endSignal.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return "OK";
    }

    /**
     * 
     * 功能描述: <br>
     * 清除节点不可用的标记
     * 
     */
    public void clearDisableFlags() {
        // shard中所有连接池
        Collection<ShardHandler> allShards = shardPools4Jedis.getAllShards();
        for (ShardHandler shard : allShards) {
            shard.clearDisableFlags();
        }
    }

	public ShardPools4Jedis getShardPools4Jedis() {
		return shardPools4Jedis;
	}
    
    
}
