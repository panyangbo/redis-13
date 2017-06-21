package teledis.config;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

/**
 * 
 * 单个Redis信息及连接池参数<br>
 * 包含客户端连接池参数信息
 * 
 * @author 
 * @see [相关/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class NodeInfo4Jedis {

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器端�?
     */
    private Integer port;

    /**
     * 不作为equals依据
     */
    private String password;

    /**
     * 数据库标�?
     */
    private int dbIndex;

    /**
     * 操作超时时间
     */
    private int timeOut;

    /**
     * 配置信息
     */
    private Config config;

    public NodeInfo4Jedis(String ip, Integer port, String password, int dbIndex, int timeOut, Config config) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.dbIndex = dbIndex;
        this.timeOut = timeOut;
        this.config = config;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public String getUrl() {
        return ip + ":" + port;
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(ip, port, dbIndex);
    }

    /**
     * 
     * 节点比较
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeInfo4Jedis) {
            NodeInfo4Jedis that = (NodeInfo4Jedis) obj;
            return ObjectUtils.equals(ip, that.ip) && ObjectUtils.equals(port, that.port)
                    && ObjectUtils.equals(dbIndex, that.dbIndex);
        } else {
            return false;
        }
    }

    /**
     * 
     * toString
     */
    @Override
    public String toString() {
        return "NodeInfo4Jedis [ip=" + ip + ", port=" + port + ", password=" + password + ", dbIndex=" + dbIndex
                + ", timeOut=" + timeOut + ", config=" + config + "]";
    }

}
