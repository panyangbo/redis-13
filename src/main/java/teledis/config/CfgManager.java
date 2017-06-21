package teledis.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teledis.exception.RedisClientException;
import teledis.util.ResourceUtils;

/**
 * 
 * 
 * 功能描述:<br>配置信息管理<p>
 * 
 * @author 
 * @version 1.0.0
 */
public class CfgManager {
    // 重试次数
    public static final int RETRYTIMES = 3;

    // 默认配置文件
    private static final String CONFIG = "teledis.xml";

    private static Logger logger = LoggerFactory.getLogger(CfgManager.class);

    protected List<ShardInfo4Jedis> lstInfo4Jedis;

    private String config = CONFIG;

    private boolean isSharding;
    
    private boolean isHashing;

    public CfgManager() {
    }

    public CfgManager(String config) {
        this.config = config;
    }

    public List<ShardInfo4Jedis> getLstInfo4Jedis() {
        return lstInfo4Jedis;
    }

    /**
     * 
     * 功能描述：是否配置分�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public boolean isSharding() {
        return isSharding;
    }

    public boolean isHashing(){
    	return isHashing;
    }
    /**
     * 
     * 功能描述�?加载配置文件
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public synchronized void loadConfig() {
        InputStream inputStream = null;
        try {
            inputStream = ResourceUtils.getResourceAsStream(config);
            Document doc = new SAXReader().read(inputStream);
            // 解析配置文件
            parserWithDoc(doc);
        } catch (IOException e) {
            logger.error("IOException!", e);
            throw new RedisClientException("IOException!", e);
        } catch (DocumentException e) {
            logger.error("SAXReader parse cache xml error!", e);
            throw new RedisClientException("SAXReader parse cache xml error!", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

    }

    /**
     * 
     * 功能描述：设置配置文档
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    public synchronized void setConfig(String cfg) {
        this.config = cfg;
    }

    /**
     * 
     * 功能描述：解析配置文�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    @SuppressWarnings("unchecked")
    private void parseShardingConfig(Element serverElement, Config poolConfig) {
        lstInfo4Jedis = new ArrayList<ShardInfo4Jedis>();
        List<Element> shards = serverElement.element("shardConfig").elements("shard");
        isHashing = Boolean.valueOf(serverElement.element("shardConfig").attributeValue("isHash"));
        for (Element shardElement : shards) {
            String shardName = shardElement.attributeValue("name").trim();
            Set<String> servtype = new HashSet<String>();
            if (!isHashing) {
            	List<Element> servtypes = shardElement.elements("servtype");
                if (servtypes.size()==0) {
                	servtype.add("nonserve");
    			}else {
    				for (Element element : servtypes) {
    	            	String serve = "".equals(element.getTextTrim())? "nonserve":element.getTextTrim();
    	            	servtype.add(serve);
    				}
    			}
			}
            List<Element> servers = shardElement.elements("server");
            if (servers.size() > 2) {
                throw new RedisClientException("Configuration error,no more than 2 servers each shard");
            }
            Set<NodeInfo4Jedis> nodes = new HashSet<NodeInfo4Jedis>();
            for (Element element : servers) {
                String ip = element.elementTextTrim("ip");
                String port = element.elementTextTrim("port");
                String password = element.elementTextTrim("password");
                String dbIndex = element.elementTextTrim("dbIndex");
                String timeOut = element.elementTextTrim("timeOut");
                if (StringUtils.isBlank(ip)) {
                    // 默认ip
                    ip = "127.0.0.1";
                }
                if (StringUtils.isBlank(port)) {
                    // 默认端口
                    port = "6379";
                }
                if (StringUtils.isBlank(dbIndex)) {
                    // 默认dbIndex
                    dbIndex = "0";
                }
                if (StringUtils.isBlank(timeOut)) {
                    // 默认操作超时时间
                    timeOut = "2000";
                }
                NodeInfo4Jedis nodeInfo4Jedis = new NodeInfo4Jedis(ip, Integer.valueOf(port), password,
                        Integer.valueOf(dbIndex), Integer.valueOf(timeOut), poolConfig);
                nodes.add(nodeInfo4Jedis);
            }
            lstInfo4Jedis.add(new ShardInfo4Jedis(shardName, nodes, servtype));
        }
        isSharding = lstInfo4Jedis.size() > 1 ? true : false;
    }

    /**
     * 
     * 功能描述�?parserWithDoc
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private void parserWithDoc(Document doc) {
        Element serverElement = doc.getRootElement();
        Config poolConfig = this.parsePoolConfig(serverElement);
        parseShardingConfig(serverElement, poolConfig);
    }

    /**
     * 
     * 功能描述�?parsePoolConfig
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private Config parsePoolConfig(Element serverElement) {
        Config config = new Config();
        config.maxWait=2000L;
        Element poolConfigTag = serverElement.element("poolConfig");
        if (poolConfigTag != null) {
            String maxIdle = poolConfigTag.elementTextTrim("maxIdle");
            String minIdle = poolConfigTag.elementTextTrim("minIdle");
            String maxActive = poolConfigTag.elementTextTrim("maxActive");
            String maxWait = poolConfigTag.elementTextTrim("maxWait");
            String whenExhaustedAction = poolConfigTag.elementTextTrim("whenExhaustedAction");
            String testOnBorrow = poolConfigTag.elementTextTrim("testOnBorrow");
            String testOnReturn = poolConfigTag.elementTextTrim("testOnReturn");
            String testWhileIdle = poolConfigTag.elementTextTrim("testWhileIdle");
            String timeBetweenEvictionRunsMillis = poolConfigTag.elementTextTrim("timeBetweenEvictionRunsMillis");
            String numTestsPerEvictionRun = poolConfigTag.elementTextTrim("numTestsPerEvictionRun");
            String minEvictableIdleTimeMillis = poolConfigTag.elementTextTrim("minEvictableIdleTimeMillis");
            String softMinEvictableIdleTimeMillis = poolConfigTag.elementTextTrim("softMinEvictableIdleTimeMillis");
            String lifo = poolConfigTag.elementTextTrim("lifo");
            if (!StringUtils.isBlank(maxIdle)) {
                config.maxIdle = Integer.valueOf(maxIdle);
            }
            if (!StringUtils.isBlank(minIdle)) {
                config.minIdle = Integer.valueOf(minIdle);
            }
            if (!StringUtils.isBlank(maxActive)) {
                config.maxActive = Integer.valueOf(maxActive);
            }
            if (!StringUtils.isBlank(maxWait)) {
                config.maxWait = Long.valueOf(maxWait);
            }
            if (!StringUtils.isBlank(lifo)) {
                config.lifo = Boolean.valueOf(lifo);
            }
            setTestParameters(config, whenExhaustedAction, testOnBorrow, testOnReturn, testWhileIdle,
                    timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis,
                    softMinEvictableIdleTimeMillis);
        }
        return config;
    }

    /**
     * 功能描述：验证连接可用�?的参数设�?
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?
     * @throw 异常描述
     * @see �?��参见的其它内�?
     */
    private void setTestParameters(Config config, String whenExhaustedAction, String testOnBorrow, String testOnReturn,
            String testWhileIdle, String timeBetweenEvictionRunsMillis, String numTestsPerEvictionRun,
            String minEvictableIdleTimeMillis, String softMinEvictableIdleTimeMillis) {

        if (!StringUtils.isBlank(testOnBorrow)) {
            // 获取连接池是否检测可用�?
            config.testOnBorrow = Boolean.valueOf(testOnBorrow);
        }

        if (!StringUtils.isBlank(testOnReturn)) {
            // 归还时是否检测可用�?
            config.testOnReturn = Boolean.valueOf(testOnReturn);
        }
        if (!StringUtils.isBlank(testWhileIdle)) {
            // 空闲时是否检测可用�?
            config.testWhileIdle = Boolean.valueOf(testWhileIdle);
        } else {
            config.testWhileIdle = true;
        }
        if (!StringUtils.isBlank(whenExhaustedAction)) {
            config.whenExhaustedAction = Byte.valueOf(whenExhaustedAction);
        }
        if (!StringUtils.isBlank(timeBetweenEvictionRunsMillis)) {
            config.timeBetweenEvictionRunsMillis = Long.valueOf(timeBetweenEvictionRunsMillis);
        } else {
            config.timeBetweenEvictionRunsMillis = 30000L;
        }
        if (!StringUtils.isBlank(numTestsPerEvictionRun)) {
            config.numTestsPerEvictionRun = Integer.valueOf(numTestsPerEvictionRun);
        } else {
            config.numTestsPerEvictionRun = -1;
        }
        if (!StringUtils.isBlank(minEvictableIdleTimeMillis)) {
            config.minEvictableIdleTimeMillis = Integer.valueOf(minEvictableIdleTimeMillis);
        } else {
            config.minEvictableIdleTimeMillis = 60000L;
        }
        if (!StringUtils.isBlank(softMinEvictableIdleTimeMillis)) {
            config.softMinEvictableIdleTimeMillis = Integer.valueOf(softMinEvictableIdleTimeMillis);
        }
    }
}
