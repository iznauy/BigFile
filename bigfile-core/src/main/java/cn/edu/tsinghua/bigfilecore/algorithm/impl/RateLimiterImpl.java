package cn.edu.tsinghua.bigfilecore.algorithm.impl;

import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public class RateLimiterImpl implements RateLimiter {

    private com.google.common.util.concurrent.RateLimiter rateLimiter;

    public RateLimiterImpl(int rate) {
        this.rateLimiter = com.google.common.util.concurrent.RateLimiter.create(rate);
    }

    @Override
    public boolean acquire(int count) {
        this.rateLimiter.acquire(count);
        return true;
    }

    @Override
    public boolean tryAcquire(int count, long timeout) {

        return this.rateLimiter.tryAcquire(count, timeout, TimeUnit.MILLISECONDS);
    }
}
