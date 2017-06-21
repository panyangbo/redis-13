package teledis.shard;

import java.util.List;

import teledis.config.ShardInfo4Jedis;
import teledis.util.TelekSharded;

/**
 * 
 * 
 * 功能描述:包括分片的连接池
 * 
 * @author 
 * @version 1.0.0
 */
public class ShardPools4Jedis extends TelekSharded<ShardHandler, ShardInfo4Jedis> {

    // 初始�?
    public ShardPools4Jedis(List<ShardInfo4Jedis> shards,boolean isHashing) {
        super(shards,isHashing);
    }
}
