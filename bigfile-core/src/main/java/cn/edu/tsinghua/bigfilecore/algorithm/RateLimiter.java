package cn.edu.tsinghua.bigfilecore.algorithm;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public interface RateLimiter {

    boolean acquire(int count);

    boolean tryAcquire(int count, long timeout);

    void resetRate(long rate);

}
