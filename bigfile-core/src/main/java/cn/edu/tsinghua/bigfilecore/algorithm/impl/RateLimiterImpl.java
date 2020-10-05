package cn.edu.tsinghua.bigfilecore.algorithm.impl;

import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public class RateLimiterImpl implements RateLimiter {

    private com.google.common.util.concurrent.RateLimiter rateLimiter;

    public RateLimiterImpl(long rate) {
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

    @Override
    public void resetRate(long rate) {
        this.rateLimiter = com.google.common.util.concurrent.RateLimiter.create(rate);
    }
}
