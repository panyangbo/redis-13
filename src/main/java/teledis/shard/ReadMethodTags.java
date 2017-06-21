package teledis.shard;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 功能描述:<br>根据方法名判断是否是读操<p>
 * 
 * @author 
 * @version 1.0.0
 */

public class ReadMethodTags {
    private static Set<String> setReadTags = new HashSet<String>();

    static {
        setReadTags.add("sort");
        setReadTags.add("get");
        setReadTags.add("exists");
        setReadTags.add("type");
        setReadTags.add("ttl");
        setReadTags.add("mget");
        setReadTags.add("keys");
        setReadTags.add("getbit");
        setReadTags.add("getrange");
        setReadTags.add("substr");
        setReadTags.add("hget");
        setReadTags.add("hmget");
        setReadTags.add("hexists");
        setReadTags.add("hlen");
        setReadTags.add("hkeys");
        setReadTags.add("hvals");
        setReadTags.add("hgetAll");
        setReadTags.add("llen");
        setReadTags.add("lrange");
        setReadTags.add("lindex");
        setReadTags.add("smembers");
        setReadTags.add("sismember");
        setReadTags.add("scard");
        setReadTags.add("srandmember");
        setReadTags.add("zrank");
        setReadTags.add("zrevrank");
        setReadTags.add("zrange");
        setReadTags.add("zrevrange");
        setReadTags.add("zcard");
        setReadTags.add("zscore");
        setReadTags.add("zcount");
        setReadTags.add("zrangeByScore");
        setReadTags.add("zrevrangeByScore");
        setReadTags.add("zrangeWithScores");
        setReadTags.add("zrevrangeWithScores");
        setReadTags.add("zrangeByScoreWithScores");
        setReadTags.add("zrevrangeByScoreWithScores");
    }

    /**
     * 
     * 功能描述：判断是否是读方法
     * 
     * @param 参数说明 返回方法 类型 <说明>
     * @return 
     * @throw 异常描述
     * @see 
     */
    public static boolean isReadMethod(String methodName) {
        return setReadTags.contains(methodName);
    }
}
