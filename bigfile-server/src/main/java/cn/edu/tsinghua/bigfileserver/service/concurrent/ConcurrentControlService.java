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

    void startDownload(String fileId);

    void startUpload(String fileId);

    void endDownload(String fileId);

    void endUpload(String fileId);

    int allocateDownloadThreads(String fileId);

    int allocateUploadThreads(String fileId);

    int getAllowedDownloadThreads(String fileId);

    int getAllowedUploadThreads(String fileId);

}
