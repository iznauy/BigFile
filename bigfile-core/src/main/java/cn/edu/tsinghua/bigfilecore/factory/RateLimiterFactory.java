package cn.edu.tsinghua.bigfilecore.factory;

import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
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

    public static RateLimiter getRateLimiter(long speed) {
        // TODO: 使用 google Guava 库中的 rateLimiter 来实现
        return null;
    }

}
