package teledis.client.impl;

import redis.clients.jedis.Jedis;

/**
 * 
 * 功能描述模板回调方法
 * 
 * @author 
 * @version 1.0.0
 * @param <R>
 */
public abstract class CallBack<R> {
    /**
     * 
     * 功能描述执行回调方法
     * 
     * @param 参数说明 返回 类型 <说明>
     * @return 返回
     * @throw 异常描述
     * @see 参见的其它内容
     */
    public abstract R invoke(Jedis jedis);
    
  
}
