package teledis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * 功能描述：安全睡眠 
 * @author 
 * @version 1.0.0
 */
public class SleepUtils {
    /**
     * 日志记录
     */
    private static Logger logger = LoggerFactory.getLogger(SleepUtils.class);

    /**
     * 
     * 功能描述�?线程睡眠
     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?     * @throw 异常描述
     * @see �?��参见的其它内�?     */
    public static void safeSleep(long time, TimeUnit unit) {
        try {
            // 线程睡眠
            unit.sleep(time);
        } catch (InterruptedException e) {
            logger.error("Interrupted while sleeping", e);
        }
    }

    /**
     * 功能描述：线程睡�?     * 
     * @param 参数说明 返回�? 类型 <说明>
     * @return 返回�?     * @throw 异常描述
     * @see �?��参见的其它内�?     */
    public static void waitUntil(long millis, Function<Boolean> waiter) {
        long totalSlept = 0;
        while (totalSlept <= millis && !waiter.apply()) {
            safeSleep(1, TimeUnit.SECONDS);
            totalSlept += 1000L;
        }
        if (!waiter.apply()) {
            throw new IllegalStateException(String.format("Execution of loop timed out after %s millis", millis));
        }
    }

    /**
     * 功能描述�?Function
     * 
     * @author 作�?13011806@cnsuning.com
     * @version 1.0.0
     */
    public interface Function<OUT> {
        OUT apply();
    }
}
