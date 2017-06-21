package servetest;

import java.util.Set;
import java.util.TreeMap;


import redis.clients.util.Hashing;
import teledis.client.impl.TelekRedisClient;
import teledis.config.ShardInfo4Jedis;

public class HashServeType {

	public TelekRedisClient client = new TelekRedisClient("redis-hash.xml");
	
	public void Set() {
		System.out.println("'test_key'的hash值----"+"'"+Hashing.MURMUR_HASH.hash("test_key")+"'");
		System.out.println(client.set("test_key", "中文测试值+englishTestValue"));
		System.out.println(client.get("test_key")+"111");
		
		System.out.println("'test_keysddsddsasfasfasfas'的hash值----"+"'"+Hashing.MURMUR_HASH.hash("test_keysddsddsasfasfasfas")+"'");
		System.out.println(client.set("test_keysddsddsasfasfasfas", "中文测试值+englishTestValue"));
		System.out.println(client.get("test_keysddsddsasfasfasfas")+"111");
		
		
		TreeMap<Long, ShardInfo4Jedis> nodes = client.getShardPools4Jedis().getNodes();
		Set<Long> ks = nodes.keySet();
		int i = 1;
		for(Long keyLong : ks){
			System.out.println("第"+i+"个节点hash值:['"+keyLong+"'];\n\t节点所属shard名称:['"+nodes.get(keyLong).getName()+"']");
			i++;
		}
	}
	
	public static void main(String[] args) {
	
		HashServeType hst = new HashServeType();
		hst.Set();
		
	}
}
