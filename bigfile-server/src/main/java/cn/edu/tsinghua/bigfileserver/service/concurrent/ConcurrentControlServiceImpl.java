package cn.edu.tsinghua.bigfileserver.service.concurrent;

import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2020-09-30.
 * Description:
 *
 * @author iznauy
 */
@Service
public class ConcurrentControlServiceImpl implements ConcurrentControlService {

    private ServerInfoService serverInfoService;

    @Autowired
    public void setServerInfoService(ServerInfoService serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    @Override
    public RateLimiter getGlobalUploadRateLimiter() {
        return null;
    }

    @Override
    public RateLimiter getGlobalDownloadRateLimiter() {
        return null;
    }

    @Override
    public RateLimiter getLocalUploadRateLimiter() {
        return null;
    }

    @Override
    public RateLimiter getLocalDownloadRateLimiter() {
        return null;
    }
}
