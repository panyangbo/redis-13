package teledis.util;

import java.util.Set;

/**
 * 
 * 
 * 1111功能描述：分片工具类, 来自Jedis，为便于区分，重新取�?
 * 
 * @author 
 * @version 1.0.0
 * @param <T>
 */
public abstract class TelekShardInfo<T> {
    
	/**
     * 该shard的权重
     */
    private int weight = 1;

    /**
     * 构造方法
     */
    public TelekShardInfo() {
    }

    /**
     * 构造方法
     * 
     * @param weight
     */
    public TelekShardInfo(int weight) {
        this.weight = weight;
    }

    /**
     * 
     * 功能描述：获取权重
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see 参见的其它内容
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * 
     * 功能描述: 创建Resource<br>
     * 初始化一个Resource
     * 
     * @return T
     * @see [相关�?方法](可�?)
     * @since [产品/模块版本](可�?)
     */
    protected abstract T createResource();

    /**
     * 
     * 功能描述：获取分片名�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public abstract String getName();
    /**
     * 功能描述：获取服务类�?
     * @return
     */
    public abstract Set<String> getServtype();
}
