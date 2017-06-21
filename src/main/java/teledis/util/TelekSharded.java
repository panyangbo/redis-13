package teledis.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.util.Hashing;
import redis.clients.util.SafeEncoder;

/**
 * 
 * 
 * 功能描述：分片工具类, 来自Jedis，为便于区分，重新取名
 * 
 * @author 
 * @version 1.0.0
 * @param <R>
 * @param <S>
 */
public class TelekSharded<R, S extends TelekShardInfo<R>> {

    // the tag is anything between {}
    public static final Pattern DEFAULT_KEY_TAG_PATTERN = Pattern.compile("\\{(.+?)\\}");

    private TreeMap<Long, S> nodes;

    private final Hashing algo;

    private final Map<TelekShardInfo<R>, R> resources = new LinkedHashMap<TelekShardInfo<R>, R>();
    
    private final Map<Set<String>, R> rs = new HashMap<Set<String>, R>();

    /**
     * The default pattern used for extracting a key tag. The pattern must have a group (between parenthesis), which
     * delimits the tag to be hashed. A null pattern avoids applying the regular expression for each lookup, improving
     * performance a little bit is key tags aren't being used.
     */
    private Pattern tagPattern = null;

    public TelekSharded(List<S> shards,boolean isHashing) {
        this(shards, Hashing.MURMUR_HASH,isHashing); // MD5 is really not good as we works
        // with 64-bits not 128
    }

    public TelekSharded(List<S> shards, Hashing algo,boolean isHashing) {
        this.algo = algo;
        initialize(shards,isHashing);
    }

    public TelekSharded(List<S> shards, Pattern tagPattern,boolean isHashing) {
        this(shards, Hashing.MURMUR_HASH, tagPattern,isHashing); // MD5 is really not good
        // as we works with
        // 64-bits not 128
    }

    public TelekSharded(List<S> shards, Hashing algo, Pattern tagPattern,boolean isHashing) {
        this.algo = algo;
        this.tagPattern = tagPattern;
        initialize(shards,isHashing);
    }

    //根据权重比创建数据服务
    private void initialize(List<S> shards,boolean isHashing) {
        int shardsSize=shards.size();
        if (isHashing) {
        	final int factor = 160;
            nodes = new TreeMap<Long, S>();
            for (int i = 0; i != shardsSize; ++i) {
                final S shardInfo = shards.get(i);
                final int weight=shardInfo.getWeight();
                final int fweight=factor*weight;
                if (shardInfo.getName() == null) {
                    for (int n = 0; n < fweight; n++) {
                        nodes.put(this.algo.hash("SHARD-" + i + "-NODE-" + n), shardInfo);
                    }
                } else {
                    for (int n = 0; n < fweight; n++) {
                        nodes.put(this.algo.hash(shardInfo.getName() + "*" + shardInfo.getWeight() + n), shardInfo);
                    }
                }
                resources.put(shardInfo, shardInfo.createResource());
            }
		}else {
			for (int i = 0; i != shardsSize; ++i) {
	            final S shardInfo = shards.get(i);
	            rs.put(shardInfo.getServtype() , shardInfo.createResource());
	        }
		}
    }

    /**
     * 
     * 功能描述�?getShard
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     *//*
    public R getShard(byte[] key) {
        return resources.get(getShardInfo(key));
    }
*/
    /**
     * 
     * 功能描述�?获取分片
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public R getShardByServe(String key) {
    	Set<Set<String>> keySet = rs.keySet();
    	for (Set<String> KS: keySet) {
    		for (String K : KS) {
				if (key.equals(K)) {
					return rs.get(KS);
				}
			}
		}
    	return null;
    }
    
    public R getShardByHash(String key) {
        return resources.get(getShardInfo(key));
    }
    
    

    /**
     * 
     * 功能描述�?获取分片信息
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public S getShardInfo(byte[] key) {
        SortedMap<Long, S> tail = nodes.tailMap(algo.hash(key));
        if (tail.size() == 0) {
            return nodes.get(nodes.firstKey());
        }
        System.out.println("操作节点hash值："+tail.firstKey());
        return tail.get(tail.firstKey());
    }
    
    /**
     * 
     * 功能描述�?getShardInfo
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public S getShardInfo(String key) {
        return getShardInfo(SafeEncoder.encode(getKeyTag(key)));
    }

    /**
     * A key tag is a special pattern inside a key that, if preset, is the only part of the key hashed in order to
     * select the server for this key.
     * 
     * @see http://code.google.com/p/redis/wiki/FAQ#I 'm_using_some_form_of_key_hashing_for_partitioning,_but_wh
     * @param key
     * @return The tag if it exists, or the original key
     */
    public String getKeyTag(String key) {
        if (tagPattern != null) {
            Matcher m = tagPattern.matcher(key);
            if (m.find()) {
                return m.group(1);
            }
        }
        return key;
    }

    /**
     * 
     * 功能描述�?getAllShardInfo
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public Collection<S> getAllShardInfo() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * 
     * 功能描述�?getAllShards
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public Collection<R> getAllShards() {
        return Collections.unmodifiableCollection(resources.values());
    }

	public TreeMap<Long, S> getNodes() {
		return nodes;
	}
    
}
