package teledis.shard;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

/**
 * 
 * 
 * 功能描述:支持选择dbIndex的Jedis连接池
 * 
 * @author 
 * @version 1.0.0
 */
public class TelekJedisPool extends Pool<Jedis> {
    private static Logger logger = LoggerFactory.getLogger(TelekJedisPool.class);

    private String host;

    private int port;

    private int dbIndex;

    private AtomicInteger errorCount = new AtomicInteger(0);

    // 是否是master
    private boolean isMaster = true;

    public TelekJedisPool(final Config poolConfig, final String host, int port, int timeout, final String password,
            int dbIndex, boolean isMaster) {
        super(poolConfig, new JedisFactory(host, port, timeout, password, dbIndex));
        this.host = host;
        this.port = port;
        this.dbIndex = dbIndex;
        this.isMaster = isMaster;
    }

    /**
     * 
     * 功能描述：是否是master
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public boolean isMaster() {
        return isMaster;
    }

    public AtomicInteger getErrorCount() {
        return errorCount;
    }

    /**
     * 
     * 功能描述：判断是否已经销�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public boolean isDestroyed() throws Exception {
        GenericObjectPool internalPool = getInternalPool();
        //池内活跃个数+对象个数为0说明被销毁
        return internalPool.getNumActive() + internalPool.getNumIdle() == 0 ? true : false;
    }

    // 获取父类的成员变�?
    public GenericObjectPool getInternalPool() throws Exception {
        Class<?> father = this.getClass().getSuperclass();
        Field f = father.getDeclaredField("internalPool");
        f.setAccessible(true);
        return (GenericObjectPool) f.get(this);
    }

    /**
     * 
     * toString
     */
    @Override
    public String toString() {
        return "[host=" + host + ", port=" + port + ", dbIndex=" + dbIndex + "]";
    }

    /**
     * 
     * hashCode
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(host, port, dbIndex);
    }

    /**
     * 对象比较
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TelekJedisPool) {
            TelekJedisPool that = (TelekJedisPool) obj;
            return ObjectUtils.equals(host, that.host) && ObjectUtils.equals(port, that.port)
                    && ObjectUtils.equals(dbIndex, that.dbIndex);
        } else {
            return false;
        }
    }

    /**
     * PoolableObjectFactory custom impl.
     */
    private static class JedisFactory extends BasePoolableObjectFactory {
        private final String host;

        private final int port;

        private final int timeout;

        private final String password;

        private final int dbIndex;

        public JedisFactory(final String host, final int port, final int timeout, final String password, int dbIndex) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = (timeout > 0) ? timeout : -1;
            this.password = password;
            this.dbIndex = dbIndex;
        }

        /**
         * 
         * 创建新连接对�?
         */
        public Object makeObject() throws Exception {
            final Jedis jedis;
            if (timeout > 0) {
                jedis = new Jedis(this.host, this.port, this.timeout);
            } else {
                jedis = new Jedis(this.host, this.port);
            }
            jedis.connect();
            if (!StringUtils.isBlank(this.password)) {
                jedis.auth(this.password);
            }
            if (dbIndex != 0) {
                jedis.select(dbIndex);
            }
            return jedis;
        }

        /**
         * 
         * �?��连接对象
         */
        public void destroyObject(final Object obj) {
            if (obj instanceof Jedis) {
                final Jedis jedis = (Jedis) obj;
                if (jedis.isConnected()) {
                    try {
                        try {
                            jedis.quit();
                        } catch (JedisException e) {
                            logger.error(e.getMessage());
                        }
                        jedis.disconnect();
                    } catch (JedisException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }

        /**
         * 
         * 验证连接是否可用
         */
        public boolean validateObject(final Object obj) {
            if (obj instanceof Jedis) {
                final Jedis jedis = (Jedis) obj;
                try {
                    return jedis.isConnected() && jedis.ping().equals("PONG");
                } catch (JedisException e) {
                    logger.error(e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
