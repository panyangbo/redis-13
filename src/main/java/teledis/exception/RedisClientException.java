package teledis.exception;

/**
 * 
 * 
 * 功能描述:<br>异常定义<p>
 * 
 * @author 
 * @version 1.0.0
 */
public class RedisClientException extends RuntimeException {
    private static final long serialVersionUID = 7460934076911268418L;

    /**
     * 构�?异常对象
     * 
     * @param msg
     */
    public RedisClientException(String msg) {
        super(msg);
    }

    /**
     * RedisClientException
     * 
     * @param exception
     */
    public RedisClientException(Throwable exception) {
        super(exception);
    }

    /**
     * RedisClientException
     * 
     * @param mag
     * @param exception
     */
    public RedisClientException(String mag, Exception exception) {
        super(mag, exception);
    }
}
