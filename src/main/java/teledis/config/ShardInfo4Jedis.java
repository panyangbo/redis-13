package teledis.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.groups.Default;

import org.apache.commons.lang3.ObjectUtils;

import teledis.shard.TelekJedisPool;
import teledis.shard.ShardHandler;
import teledis.util.TelekShardInfo;

/**
 * 
 * 
 * 功能描述:<br>分片的Redis节点信息<p>
 * 
 * @author 
 * @version 1.0.0
 */
public class ShardInfo4Jedis extends TelekShardInfo<ShardHandler> {

    /**
     * shard的唯一性
     */
    private final String shardName;

    /**
     * 每个分片的Redis节点信息
     */
    private final Set<NodeInfo4Jedis> nodes = new HashSet<NodeInfo4Jedis>();
    
    /**
     * 服务对象
     */
    private final Set<String> servtype;

    /**
     * 构造方法
     * 
     * @param shardName
     * @param nodes
     */
    public ShardInfo4Jedis(String shardName, Collection<NodeInfo4Jedis> nodes,Set<String> servtype) {
        this.shardName = shardName;
        this.nodes.addAll(nodes);
        this.servtype = servtype;
    }

    /**
     * 
     * 返回shardNama
     */
    @Override
    public String getName() {
        return shardName;
    }

    @Override
    public Set<String> getServtype(){
    	return servtype;
    }
    
    /**
     * 
     * toString
     */
    @Override
    public String toString() {
        return "ShardInfo [shardName=" + shardName + ", nodes=" + nodes + "]";
    }

    /**
     * 
     * hashCode
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(shardName, nodes);
    }

    /**
     * 
     * equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShardInfo4Jedis) {
            ShardInfo4Jedis that = (ShardInfo4Jedis) obj;
            return ObjectUtils.equals(nodes, that.nodes) && ObjectUtils.equals(shardName, that.shardName);
        } else {
            return false;
        }
    }

    /**
     * 创建本shard的处理资源
     */
    protected ShardHandler createResource() {
        List<TelekJedisPool> masterPools = new ArrayList<TelekJedisPool>();
        for (NodeInfo4Jedis nodeInfo : nodes) {
        	TelekJedisPool jedisPool = new TelekJedisPool(nodeInfo.getConfig(), nodeInfo.getIp(), nodeInfo.getPort(),
                    nodeInfo.getTimeOut(), nodeInfo.getPassword(), nodeInfo.getDbIndex(), true);
            masterPools.add(jedisPool);
        }
        return new ShardHandler(masterPools, this);
    }
}
