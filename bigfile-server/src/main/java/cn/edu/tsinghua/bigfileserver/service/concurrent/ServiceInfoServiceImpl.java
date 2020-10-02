package cn.edu.tsinghua.bigfileserver.service.concurrent;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2020-09-30.
 * Description:
 *
 * @author iznauy
 */
@Service
public class ServiceInfoServiceImpl implements ServerInfoService {

    private AtomicInteger downloadThreadCount = new AtomicInteger(0);

    private AtomicInteger uploadThreadCount = new AtomicInteger(0);

    private Map<String, AtomicInteger> uploadCountMap = new ConcurrentHashMap<>();

    private Map<String, AtomicInteger> downloadCountMap = new ConcurrentHashMap<>();

    @Override
    public int getDownloadThreadCount() {
        return downloadThreadCount.get();
    }

    @Override
    public int getUploadThreadCount() {
        return uploadThreadCount.get();
    }

    @Override
    public void startDownload(String fileId) {

    }

    @Override
    public void startUpload(String fileId) {

    }

    @Override
    public void endDownload(String fileId) {

    }

    @Override
    public void endUpload(String fileId) {

    }
}
