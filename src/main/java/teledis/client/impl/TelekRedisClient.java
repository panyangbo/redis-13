package teledis.client.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import teledis.client.ITeledisClient;
import teledis.exception.RedisClientException;

/**
 * 
 * 功能描述:默认的String操作接口实现
 * 
 * @author 
 */
public class TelekRedisClient extends AbstractClient implements ITeledisClient{
	
	public TelekRedisClient() {
        super();
    }

    public TelekRedisClient(String config) {
        super(config);
    }

    private <R> R performFunction(String S, CallBack<R> callBack) throws NullPointerException {
    	//initParam(keyMap);
    	if(isHashing()){
    		return shardPools4Jedis.getShardByHash(S).execute(callBack);
    	} else{
    		return shardPools4Jedis.getShardByServe(S).execute(callBack);
    	}
    	
    }

    /**
     * 指定serve-shard<br>
     * set方法
     * <p>
     * @param servtype
     * @param key
     * @param value
     * @return String, 操作成功 ok
     * @see <a href="http://code.google.com/p/redis/wiki/SetCommand">google解释</a>
     */
    @Override
    public String set(final String servtype, final String key ,final String value) {
        return this.performFunction(servtype, new CallBack<String>() {
            // set
            public String invoke(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }
    
    /**
     * 一致性hash<br>
     * set方法
     * <p>
     * @param key
     * @param value
     * @see #set(String, String, String)
     */
    @Override
    public String set(final String key ,final String value) {
        return this.performFunction(key, new CallBack<String>() {
            // set
            public String invoke(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 指定serve-shard<br>
     * 支持通道模式的get方法
     * <p>
     * @param servtype
     * @param keys
     * @see #get(String)
     */
    @Override
    public List<Object> get(final String servtype, final Set<String> keys) {
    	if (isHashing()) {
			throw new RedisClientException("一致性hash模式不支持此方法");
		}
    	return this.performFunction(servtype, new CallBack<List<Object>>() {
            public List<Object> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
				Iterator<String> iterator = keys.iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					pipeline.get(key);
				}
				return pipeline.syncAndReturnAll();
            }
        });
    }
    
    /**
     * 指定serve-shard<br>
     * get方法
     * <p>
     * @param servtype
     * @param key
     * @see #get(String)
     */
    @Override
    public String get(final String servtype, final String key){
    	return this.performFunction(servtype, new CallBack<String>(){
    		public String invoke(Jedis jedis) {
				return jedis.get(key);
			}
    	});
    }
    
    /**
     * �?��性hash<br>
     * get方法
     * <p>
     * @param key
     * @return key对应的value
     * @see <a href="http://code.google.com/p/redis/wiki/SetCommand">google解释</a>
     */
    @Override
    public String get(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 一致性hash<br>
     * exists方法
     * <p>
     * 时间复杂： O(1)
     * 
     * @param key
     * @return Boolean 返回, true存在;false不存在
     *//*
    @Override
    public Boolean exists(final String key) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }
    
    *//**
     * exists方法
     * 
     * 时间复杂�? O(1)
     * 
     * @param key
     * @param servtype
     * @return Boolean 返回�?, true存在;false不存�?
     *//*
    @Override
    public Boolean exists(final String servtype , final String key) {
		return this.performFunction(servtype, new CallBack<Boolean>() {
			public Boolean invoke(Jedis jedis){
				return jedis.exists(key);
			}
		});
	}

    *//**
     * �?��性hash<br>
     * type方法<p>
     * 
     * 时间复杂�? O(1)
     * 
     * @param key
     * @return 返回key的类�?
     * @see <a href="http://code.google.com/p/redis/wiki/TypeCommand">google解释</a>
     *//*
    @Override
    public String type(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.type(key);
            }
        });
    }
    
    *//**
     * 指定serve-shard<br>
     * type方法
     * <p>
     * @see #type(String)
     *//*
    @Override
    public String type(final String servtype, final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.type(key);
            }
        });
    }

    *//**
     * �?��性hash<br>
     * expire方法<p>
     * 时间复杂�? O(1)
     * 
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     * 
     * @since redis 2.1.3
     * @param key
     * @param seconds
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since the key already has
     *         an associated timeout (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will happily update
     *         the timeout), or the key does not exist.
     *//*
    @Override
    public Long expire(final String key, final int seconds) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    *//**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but instead to get the number of seconds
     * representing the Time To Live of the key as a second argument (that is a relative way of specifing the TTL), it
     * takes an absolute one in the form of a UNIX timestamp (Number of seconds elapsed since 1 Gen 1970).
     * <p>
     * EXPIREAT was introduced in order to implement the Append Only File persistence mode so that EXPIRE commands are
     * automatically translated into EXPIREAT commands for the append only file. Of course EXPIREAT can also used by
     * programmers that need a way to simply specify that a given key should expire at a given time in the future.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire set. It is also
     * possible to undo the expire at all turning the key into a normal key using the {@link #persist(String) PERSIST}
     * command.
     * <p>
     * Time complexity: O(1)
     * 
     * @see <ahref="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     * 
     * @param key
     * @param unixTime
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since the key already has
     *         an associated timeout (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will happily update
     *         the timeout), or the key does not exist.
     *//*
    @Override
    public Long expireAt(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.expireAt(key, unixTime);
            }
        });
    }

    *//**
     * The TTL command returns the remaining time to live in seconds of a key that has an {@link #expire(String, int)
     * EXPIRE} set. This introspection capability allows a Redis client to check how many seconds a given key will
     * continue to be part of the dataset.
     * 
     * @param key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an EXPIRE. If the Key does
     *         not exists or does not have an associated expire, -1 is returned.
     *//*
    @Override
    public Long ttl(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    *//**
     * Sets or clears the bit at offset in the string value stored at key
     * 
     * @param key
     * @param offset
     * @param value
     * @return
     *//*
    @Override
    public Boolean setbit(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
            	Boolean value = ConstantsUtil.booleanUtil(redis_params.value.getObject(keyMap));
                return jedis.setbit(key, offset, value);
            }
        });
    }

    *//**
     * Sets or clears the bit at offset in the string value stored at key
     * 
     * @param key
     * @param offset
     * @param value
     * @return
     *//*
    @Override
    public Boolean getbit(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.getbit(key, offset);
            }
        });
    }

    *//**
     * setrange
     *//*
    @Override
    public Long setrange(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.setrange(key, offset, value);
            }
        });
    }

    @Override
    public String getrange(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.getrange(key, startOffset, endOffset);
            }
        });
    }

    *//**
     * GETSET is an atomic set this value and return the old value command. Set key to the string value and return the
     * old value stored at key. The string can't be longer than 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     * 
     * @param key
     * @param value
     * @return Bulk reply
     *//*
    @Override
    public String getSet(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.getSet(key, value);
            }
        });
    }

    *//**
     * SETNX works exactly like {@link #set(String, String) SET} with the only difference that if the key already exists
     * no operation is performed. SETNX actually means "SET if Not eXists".
     * <p>
     * Time complexity: O(1)
     * 
     * @param key
     * @param value
     * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
     *//*
    @Override
    public Long setnx(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    *//**
     * Get the value of the specified key. If the key does not exist the special value 'nil' is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string values.
     * <p>
     * Time complexity: O(1)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public String setex(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    *//**
     * IDECRBY work just like {@link #decr(String) INCR} but instead to decrement by 1 the decrement is integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #decr(String)
     * @see #incrBy(String, int)
     * 
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     *//*
    @Override
    public Long decrBy(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.decrBy(key, integer);
            }
        });
    }

    *//**
     * Decrement the number stored at key by one. If the key does not exist or contains a value of a wrong type, set the
     * key to the value of "0" before to perform the decrement operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #incrBy(String, int)
     * @see #decrBy(String, int)
     * 
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     *//*
    @Override
    public Long decr(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    *//**
     * INCRBY work just like {@link #incr(String) INCR} but instead to increment by 1 the increment is integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #decr(String)
     * @see #decrBy(String, int)
     * 
     * @param key
     * @param integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     *//*
    @Override
    public Long incrBy(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.incrBy(key, integer);
            }
        });
    }

    *//**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a wrong type, set the
     * key to the value of "0" before to perform the increment operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incrBy(String, int)
     * @see #decr(String)
     * @see #decrBy(String, int)
     * 
     * @param key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     *//*
    @Override
    public Long incr(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    *//**
     * If the key already exists and is a string, this command appends the provided value at the end of the string. If
     * the key does not exist it is created and set as an empty string, so APPEND will be very similar to SET in this
     * special case.
     * <p>
     * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is small and the already
     * present value is of any size, since the dynamic string library used by Redis will double the free space available
     * on every reallocation.
     * 
     * @param key
     * @param value
     * @return Integer reply, specifically the total length of the string after the append operation.
     *//*
    @Override
    public Long append(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.append(key, value);
            }
        });
    }

    *//**
     * Return a subset of the string from offset start to offset end (both offsets are inclusive). Negative offsets can
     * be used in order to provide an offset starting from the end of the string. So -1 means the last char, -2 the
     * penultimate and so forth.
     * <p>
     * The function handles out of range requests without raising an error, but just limiting the resulting range to the
     * actual length of the string.
     * <p>
     * Time complexity: O(start+n) (with start being the start index and n the total length of the requested range).
     * Note that the lookup part of this command is O(1) so for small strings this is actually an O(1) command.
     * 
     * @param key
     * @param start
     * @param end
     * @return Bulk reply
     *//*
    @Override
    public String substr(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.substr(key, start, end);
            }
        });
    }

    *//**
     * 
     * Set the specified hash field to the specified value.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is returned, otherwise
     *         if a new field is created 1 is returned.
     *//*
    @Override
    public Long hset(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    *//**
     * If key holds a hash, retrieve the value associated to the specified field.
     * <p>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @return Bulk reply
     *//*
    @Override
    public String hget(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    *//**
     * 
     * Set the specified hash field to the specified value if the field not exists. <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is returned.
     *//*
    @Override
    public Long hsetnx(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hsetnx(key, field, value);
            }
        });
    }

    *//**
     * Set the respective fields to the respective values. HMSET replaces old values with new values.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     * 
     * @param key
     * @param hash
     * @return Return OK or Exception if hash is empty
     *//*
    @Override
    public List<Object> hmset(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<Object>>() {
            public List<Object> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
            	for (Map map : maplist) {
            		String key = (String) map.get(redis_params.key);
            		map.remove(redis_params.key);
            		pipeline.hmset(key, map);
				}
            	return pipeline.syncAndReturnAll();
            }
        });
    }

    *//**
     * Retrieve the values associated to the specified fields.
     * <p>
     * If some of the specified fields do not exist, nil values are returned. Non existing keys are considered like
     * empty hashes.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     * 
     * @param key
     * @param fields
     * @return Multi Bulk Reply specifically a list of all the values associated with the specified fields, in the same
     *         order of the request.
     *//*
    @Override
    public List<String> hmget(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
            	for (String key : keys) {
					pipeline.hmget(key, fields);
				}
                return jedis.hmget(key, fields);
            }
        });
    }

    *//**
     * Increment the number stored at field in the hash at key by value. If key does not exist, a new key holding a hash
     * is created. If field does not exist or holds a string, the value is set to 0 before applying the operation. Since
     * the value argument is signed you can use this command to perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBY is limited to 64 bit signed integers.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @param value
     * @return Integer reply The new value at field after the increment operation.
     *//*
    @Override
    public Long hincrBy(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
            	long value = ConstantsUtil.longUtil(redis_params.value.getObject(keyMap));
                return jedis.hincrBy(key, field, value);
            }
        });
    }

    *//**
     * Test for existence of a specified field in a hash.
     * 
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key is not found or the
     *         field is not present.
     *//*
    @Override
    public Boolean hexists(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    *//**
     * Remove the specified field from an hash stored at key.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param field
     * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is returned and no
     *         operation is performed.
     * @since jedis-2.1.0
     *//*
    @Override
    public Long hdel(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hdel(key, fields);
            }
        });
    }

    *//**
     * Return the number of items in a hash.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @return The number of entries (fields) contained in the hash stored at key. If the specified key does not exist,
     *         0 is returned assuming an empty hash.
     *//*
    @Override
    public Long hlen(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public Set<String> hkeys(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public List<String> hvals(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public List<Object> hgetAll(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<Object>>() {
            public List<Object> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
            	for (String key : keys) {
					pipeline.hgetAll(key);
				}
                return pipeline.syncAndReturnAll();
            }
        });
    }

    *//**
     * {@inheritDoc}
     * @since jedis-2.1.0
     * 
     *//*
    @Override
    public Long rpush(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.rpush(key, values);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public Long lpush(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.lpush(key, values);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public Long llen(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public List<String> lrange(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.lrange(key, start, end);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public String ltrim(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.ltrim(key, start, end);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public String lindex(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lindex(key, index);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public String lset(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lset(key, index, value);
            }
        });
    }

    *//**
     * Remove the first count occurrences of the value element from the list. If count is zero all the elements are
     * removed. If count is negative elements are removed from tail to head, instead to go from head to tail that is the
     * normal behaviour. So for example LREM with count -2 and hello as value to remove against the list
     * (a,b,c,hello,x,hello,hello) will lave the list (a,b,c,hello,x). The number of removed elements is returned as an
     * integer, see below for more information about the returned value. Note that non existing keys are considered like
     * empty lists by LREM, so LREM against non existing keys will always return 0.
     * <p>
     * Time complexity: O(N) (with N being the length of the list)
     * 
     * @param key
     * @param count
     * @param value
     * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
     *//*
    @Override
    public Long lrem(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.lrem(key, count, value);
            }
        });
    }

    *//**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list
     * contains the elements "a","b","c" LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @see #rpop(String)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public String lpop(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    *//**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list
     * contains the elements "a","b","c" LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @see #lpop(String)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public String rpop(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.rpop(key);
            }
        });
    }

    *//**
     * Add the specified member to the set value stored at key. If member is already a member of the set no operation is
     * performed. If key does not exist a new set with the specified member as sole member is created. If the key exists
     * but does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was already a member of the
     *         set
     *//*
    @Override
    public List<Object> sadd(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<Object>>() {
            public List<Object> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
            	Set<String> keys = membersMap.keySet();
            	for (String key : keys) {
					HashSet<String> memberSet = membersMap.get(key);
					for (String member : memberSet) {
						pipeline.sadd(key, member);
					}
				}
            	return pipeline.syncAndReturnAll();
            }
        });
    }

    *//**
     * Return all the members (elements) of the set value stored at key. This is just syntax glue for
     * {@link #sinter(String...) SINTER}.
     * <p>
     * Time complexity O(N)
     * 
     * @param key
     * @return Multi bulk reply
     *//*
    @Override
    public Set<String> smembers(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    *//**
     * Remove the specified member from the set value stored at key. If member was not a member of the set no operation
     * is performed. If key does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was not a member of
     *         the set
     *//*
    @Override
    public Long srem(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.srem(key, members);
            }
        });
    }

    *//**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key does not exist, a
     * nil object is returned.
     * <p>
     * The {@link #srandmember(String)} command does a similar work but the returned element is not removed from the
     * Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public String spop(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.spop(key);
            }
        });
    }

    *//**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key does not exist, a
     * nil object is returned.
     * <p>
     * The {@link #srandmember(String)} command does a similar work but the returned element is not removed from the
     * Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public Long scard(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    *//**
     * Return 1 if member is a member of the set stored at key, otherwise 0 is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element is not a member of
     *         the set OR if the key does not exist
     *//*
    @Override
    public Boolean sismember(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    *//**
     * Return a random element from a Set, without removing the element. If the Set is empty or the key does not exist,
     * a nil object is returned.
     * <p>
     * The SPOP command does a similar work but the returned element is popped (removed) from the Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @return Bulk reply
     *//*
    @Override
    public String srandmember(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.srandmember(key);
            }
        });
    }

    *//**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member is already a
     * member of the sorted set the score is updated, and the element reinserted in the right position to ensure
     * sorting. If key does not exist a new sorted set with the specified member as sole member is crated. If the key
     * exists but does not hold a sorted set value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * @param key
     * @param score
     * @param member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was already a member of the
     *         sorted set and the score was updated
     *//*
    @Override
    public Long zadd(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zadd(key, score, member);
            }
        });
    }

    *//**
     * {@inheritDoc}
     *//*
    @Override
    public Set<String> zrange(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrange(key, start, end);
            }
        });
    }

    *//**
     * Remove the specified member from the sorted set value stored at key. If member was not a member of the set no
     * operation is performed. If key does not not hold a set value an error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * 
     * 
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was not a member of
     *         the set
     *//*
    @Override
    public Long zrem(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrem(key, members);
            }
        });
    }

    *//**
     * If member already exists in the sorted set adds the increment to its score and updates the position of the
     * element in the sorted set accordingly. If member does not already exist in the sorted set it is added with
     * increment as score (that is, like if the previous score was virtually zero). If key does not exist a new sorted
     * set with the specified member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number. It's possible to
     * provide a negative value to perform a decrement.
     * <p>
     * For an introduction to sorted sets check the Introduction to Redis data types page.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * @param key
     * @param score
     * @param member
     * @return The new score
     *//*
    @Override
    public Double zincrby(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Double>() {
            public Double invoke(Jedis jedis) {
                return jedis.zincrby(key, score, member);
            }
        });
    }

    *//**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from low to high.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned. The returned rank
     * (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     * 
     * @see #zrevrank(String, String)
     * 
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer reply if the
     *         element exists. A nil bulk reply if there is no such element.
     *//*
    @Override
    public Long zrank(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrank(key, member);
            }
        });
    }

    *//**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from high to low.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned. The returned rank
     * (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     * 
     * @see #zrank(String, String)
     * 
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer reply if the
     *         element exists. A nil bulk reply if there is no such element.
     *//*
    @Override
    public Long zrevrank(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrevrank(key, member);
            }
        });
    }

    *//**
     * zrevrange
     * @since jedis-2.1.0
     *//*
    @Override
    public Set<String> zrevrange(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrevrange(key, start, end);
            }
        });
    }

    *//**
     * zrangeWithScores
     * @since jedis-2.1.0
     *//*
    @Override
    public Set<Tuple> zrangeWithScores(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrangeWithScores(key, start, end);
            }
        });
    }

    *//**
     * zrevrangeWithScores
     * @since jedis-2.1.0
     *//*
    @Override
    public Set<Tuple> zrevrangeWithScores(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrevrangeWithScores(key, start, end);
            }
        });
    }

    *//**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is returned, like for empty
     * sorted sets.
     * <p>
     * Time complexity O(1)
     * 
     * @param key
     * @return the cardinality (number of elements) of the set as an integer.
     *//*
    @Override
    public Long zcard(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    *//**
     * Return the score of the specified element of the sorted set at key. If the specified element does not exist in
     * the sorted set, or the key does not exist at all, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key
     * @param member
     * @return the score
     *//*
    @Override
    public Double zscore(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Double>() {
            public Double invoke(Jedis jedis) {
                return jedis.zscore(key, member);
            }
        });
    }

    *//**
     * Sort a Set or a List.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key. By default sorting is numeric with
     * elements being compared as double precision floating point numbers. This is the simplest form of SORT.
     * 
     * @see #sort(String, String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     * 
     * 
     * @param key
     * @return Assuming the Set/List at key contains a list of numbers, the return value will be the list of numbers
     *         ordered from the smallest to the biggest number.
     *//*
    @Override
    public List<String> sort(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
            	if (sortingParameters!=null) {
            		return jedis.sort(key, sortingParameters);
				}
                return jedis.sort(key);
            }
        });
    }

    *//**
     * Sort a Set or a List accordingly to the specified parameters.
     * <p>
     * <b>examples:</b>
     * <p>
     * Given are the following sets and key/values:
     * 
     * <pre>
     * x = [1, 2, 3]
     * y = [a, b, c]
     * 
     * k1 = z
     * k2 = y
     * k3 = x
     * 
     * w1 = 9
     * w2 = 8
     * w3 = 7
     * </pre>
     * 
     * Sort Order:
     * 
     * <pre>
     * sort(x) or sort(x, sp.asc())
     * -> [1, 2, 3]
     * 
     * sort(x, sp.desc())
     * -> [3, 2, 1]
     * 
     * sort(y)
     * -> [c, a, b]
     * 
     * sort(y, sp.alpha())
     * -> [a, b, c]
     * 
     * sort(y, sp.alpha().desc())
     * -> [c, a, b]
     * </pre>
     * 
     * Limit (e.g. for Pagination):
     * 
     * <pre>
     * sort(x, sp.limit(0, 2))
     * -> [1, 2]
     * 
     * sort(y, sp.alpha().desc().limit(1, 2))
     * -> [b, a]
     * </pre>
     * 
     * Sorting by external keys:
     * 
     * <pre>
     * sort(x, sb.by(w*))
     * -> [3, 2, 1]
     * 
     * sort(x, sb.by(w*).desc())
     * -> [1, 2, 3]
     * </pre>
     * 
     * Getting external keys:
     * 
     * <pre>
     * sort(x, sp.by(w*).get(k*))
     * -> [x, y, z]
     * 
     * sort(x, sp.by(w*).get(#).get(k*))
     * -> [3, x, 2, y, 1, z]
     * </pre>
     * 
     * @see #sort(String)
     * @see #sort(String, SortingParams, String)
     * 
     * @param key
     * @param sortingParameters
     * @return a list of sorted elements.
     *//*
    @Override
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.sort(key, sortingParameters);
            }
        });
    }

    *//**
     * zcount
     *//*
    @Override
    public Long zcount(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zcount(key, min, max);
            }
        });
    }

    *//**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, String, String)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     *//*
    @Override
    public Set<String> zrangeByScore(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
            	int offset = ConstantsUtil.integerUtil(keyMap.get(redis_params.offset));
            	int count = ConstantsUtil.integerUtil(keyMap.get(redis_params.count));
            	if (offset!=-1 &&count!=-1) {
            		return jedis.zrangeByScore(key, max, min, offset, count);
				}
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }

    *//**
     * zrevrangeByScore
     *//*
    @Override
    public Set<String> zrevrangeByScore(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
            	int offset = ConstantsUtil.integerUtil(keyMap.get(redis_params.offset));
            	int count = ConstantsUtil.integerUtil(keyMap.get(redis_params.count));
            	if (offset!=-1 &&count!=-1) {
            		return jedis.zrevrangeByScore(key, max, min, offset, count);
				}
                return jedis.zrevrangeByScore(key, max, min);
            }
        });
    }

    *//**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     *//*
    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrangeByScore(key, max, min, offset, count);
            }
        });
    }

    *//**
     * zrevrangeByScore
     *//*
    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrevrangeByScore(key, max, min, offset, count);
            }
        });
    }

    *//**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end are 0-based with rank
     * 0 being the element with the lowest score. Both start and end can be negative numbers, where they indicate
     * offsets starting at the element with the highest rank. For example: -1 is the element with the highest score, -2
     * the element with the second highest score and so forth.
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements removed by the operation
     * 
     * @since
     * 
     *//*
    @Override
    public Long zremrangeByRank(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zremrangeByRank(key, start, end);
            }
        });
    }

    *//**
     * Remove all the elements in the sorted set at key with a score between min and max (including elements with score
     * equal to min or max).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements removed by the
     * operation
     * 
     * @param key
     * @param start
     * @param end
     * @return Integer reply, specifically the number of elements removed.
     *//*
    @Override
    public Long zremrangeByScore(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
            	double start = ConstantsUtil.doubleUtil(keyMap.get(redis_params.start));
            	double end = ConstantsUtil.doubleUtil(keyMap.get(redis_params.end));
                return jedis.zremrangeByScore(key, start, end);
            }
        });
    }

    *//**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     *//*
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
            	int offset = ConstantsUtil.integerUtil(keyMap.get(redis_params.offset));
            	int count = ConstantsUtil.integerUtil(keyMap.get(redis_params.count));
            	if (offset!=-1 &&count!=-1) {
            		return jedis.zrangeByScoreWithScores(key, max, min, offset, count);
				}
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        });
    }

    *//**
     * zrevrangeByScoreWithScores
     *//*
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
            	int offset = ConstantsUtil.integerUtil(keyMap.get(redis_params.offset));
            	int count = ConstantsUtil.integerUtil(keyMap.get(redis_params.count));
            	if (offset!=-1 &&count!=-1) {
            		return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
				}
                return jedis.zrevrangeByScoreWithScores(key, max, min);
            }
        });
    }

    *//**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key
     * @param min
     * @param max
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     *//*
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    *//**
     * zrevrangeByScoreWithScores
     *//*
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min,
            final int offset, final int count) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    *//**
     * linsert
     *//*
    @Override
    public Long linsert(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.linsert(key, where, pivot, value);
            }
        });
    }

    *//**
     * Remove the specified keys. If a given key does not exist no operation is performed for this key. The command
     * returns the number of keys removed.
     * 
     * Time complexity: O(1)
     * 
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed 0 if none of the
     *         specified key existed
     *//*
    @Override
    public List<Object> del(final Map<String, Object> keyMap) {
        return this.performFunction(keyMap, new CallBack<List<Object>>() {
            public List<Object> invoke(Jedis jedis) {
            	Pipeline pipeline = jedis.pipelined();
				for (String key : keys) {
					pipeline.del(key);
				}
				return pipeline.syncAndReturnAll();
            }
        });
    }

    *//**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key does not exist an
     * empty list is created just before the append operation. If the key exists but is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     * 
     * @see Jedis#rpush(String, String)
     * 
     * @param key
     * @param string
     * @return Integer reply, specifically, the number of elements inside the list after the push operation.
     *//*
    @Override
    public Long lpush(final String key, final String... fields) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                long r = 0;
                for (String field : fields) {
                    r = r + jedis.lpush(key, field);
                }
                return r;
            }
        });
    }

    *//**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key does not exist an
     * empty list is created just before the append operation. If the key exists but is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     * 
     * @see Jedis#lpush(String, String)
     * 
     * @param key
     * @param string
     * @return Integer reply, specifically, the number of elements inside the list after the push operation.
     *//*
    @Override
    public Long rpush(final String key, final String... fields) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                long r = 0;
                for (String field : fields) {
                    r = r + jedis.rpush(key, field);
                }
                return r;
            }
        });
    }

    *//**
     * Returns all the keys matching the glob-style pattern as space separated strings. For example if you have in the
     * database the keys "foo" and "foobar" the command "KEYS foo*" will return "foo foobar".
     * <p>
     * Note that while the time complexity for this operation is O(n) the constant times are pretty low. For example
     * Redis running on an entry level laptop can scan a 1 million keys database in 40 milliseconds. <b>Still it's
     * better to consider this one of the slow commands that may ruin the DB performance if not used with care.</b>
     * <p>
     * In other words this command is intended only for debugging and special operations like creating a script to
     * change the DB schema. Don't use it in your normal code. Use Redis Sets in order to group together a subset of
     * objects.
     * <p>
     * Glob style patterns examples:
     * <ul>
     * <li>h?llo will match hello hallo hhllo
     * <li>h*llo will match hllo heeeello
     * <li>h[ae]llo will match hello and hallo, but not hillo
     * </ul>
     * <p>
     * Use \ to escape special chars if you want to match them verbatim.
     * <p>
     * Time complexity: O(n) (with n being the number of keys in the DB, and assuming keys and pattern of limited
     * length)
     * 
     * @param pattern
     * @return Multi bulk reply
     *//*
    @Override
    public Set<String> keys(final String pattern) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    *//**
     * Set the the respective keys to the respective values. MSET will replace old values with new values, while
     * {@link #msetnx(String...) MSETNX} will not perform any operation at all even if just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing different fields of an
     * unique logic object in a way that ensures that either all the fields or none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B are modified,
     * another client talking to Redis can either see the changes to both A and B at once, or no modification at all.
     * 
     * @see #msetnx(String...)
     * 
     * @param keysvalues
     * @return Status code reply Basically +OK as MSET can't fail
     *//*
    @Override
    public String mset(final Map<String, String> keyValues) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.mset(CacheUtils.smapToArray(keyValues));
            }
        });
    }

    *//**
     * Get the values of all the specified keys. If one or more keys dont exist or is not of type String, a 'nil' value
     * is returned instead of the value of the specified key, but the operation never fails.
     * <p>
     * Time complexity: O(1) for every key
     * 
     * @param keys
     * @return Multi bulk reply
     *//*
    @Override
    public List<String> mget(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.mget(keys);
            }
        });
    }

    *//**
     * Return the members of a set resulting from the intersection of all the sets hold at the specified keys. Like in
     * {@link #lrange(String, int, int) LRANGE} the result is sent to the client as a multi-bulk reply (see the protocol
     * specification for more information). If just a single key is specified, then this command produces the same
     * result as {@link #smembers(String) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER.
     * <p>
     * Non existing keys are considered like empty sets, so if one of the keys is missing an empty set is returned
     * (since the intersection with an empty set always is an empty set).
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the number of sets
     * 
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     *//*
    @Override
    public Set<String> sinter(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sinter(keys);
            }
        });
    }

    *//**
     * This commnad works exactly like {@link #sinter(String...) SINTER} but instead of being returned the resulting set
     * is sotred as dstkey.
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the number of sets
     * 
     * @param dstkey
     * @param keys
     * @return Status code reply
     *//*
    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sinterstore(dstkey, keys);
            }
        });
    }

    *//**
     * Return the members of a set resulting from the union of all the sets hold at the specified keys. Like in
     * {@link #lrange(String, int, int) LRANGE} the result is sent to the client as a multi-bulk reply (see the protocol
     * specification for more information). If just a single key is specified, then this command produces the same
     * result as {@link #smembers(String) SMEMBERS}.
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     * 
     * @param keys
     * @return Multi bulk reply, specifically the list of common elements.
     *//*
    @Override
    public Set<String> sunion(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sunion(keys);
            }
        });
    }

    *//**
     * This command works exactly like {@link #sunion(String...) SUNION} but instead of being returned the resulting set
     * is stored as dstkey. Any existing value in dstkey will be over-written.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     * 
     * @param dstkey
     * @param keys
     * @return Status code reply
     *//*
    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sunionstore(dstkey, keys);
            }
        });
    }

    *//**
     * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN
     * <p>
     * <b>Example:</b>
     * 
     * <pre>
     * key1 = [x, a, b, c]
     * key2 = [c]
     * key3 = [a, d]
     * SDIFF key1,key2,key3 => [x, b]
     * </pre>
     * 
     * Non existing keys are considered like empty sets.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) with N being the total number of elements of all the sets
     * 
     * @param keys
     * @return Return the members of a set resulting from the difference between the first set provided and all the
     *         successive sets.
     *//*
    @Override
    public Set<String> sdiff(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sdiff(keys);
            }
        });
    }

    *//**
     * This command works exactly like {@link #sdiff(String...) SDIFF} but instead of being returned the resulting set
     * is stored in dstkey.
     * 
     * @param dstkey
     * @param keys
     * @return Status code reply
     *//*
    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sdiffstore(dstkey, keys);
            }
        });
    }

    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zinterstore(dstkey, sets);
            }
        });
    }

    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zinterstore(dstkey, params, sets);
            }
        });
    }

    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zunionstore(dstkey, sets);
            }
        });
    }

    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zunionstore(dstkey, params, sets);
            }
        });
    }

*/
}
