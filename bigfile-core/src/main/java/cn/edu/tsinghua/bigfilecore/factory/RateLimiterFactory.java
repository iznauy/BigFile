package cn.edu.tsinghua.bigfilecore.factory;

import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
import cn.edu.tsinghua.bigfilecore.algorithm.impl.RateLimiterImpl;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public class RateLimiterFactory {

    private RateLimiterFactory() {

    }

    public static RateLimiter getRateLimiter(int speed) {
        return new RateLimiterImpl(speed);
    }

}
