package cn.edu.tsinghua.bigfileserver.service.concurrent;

import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;

/**
 * Created on 2020-09-30.
 * Description:
 *
 * @author iznauy
 */
public interface ConcurrentControlService {

    RateLimiter getGlobalUploadRateLimiter();

    RateLimiter getGlobalDownloadRateLimiter();

    RateLimiter getLocalUploadRateLimiter();

    RateLimiter getLocalDownloadRateLimiter();



}
