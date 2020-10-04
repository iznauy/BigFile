package cn.edu.tsinghua.bigfileserver.service.concurrent;

import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
import cn.edu.tsinghua.bigfilecore.factory.RateLimiterFactory;
import cn.edu.tsinghua.bigfileserver.exception.BigFileTooMuchConcurrentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2020-09-30.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Service
@EnableScheduling
public class ConcurrentControlServiceImpl implements ConcurrentControlService {

    @Value("${bigfile.resource.bandwidth.upload}")
    private long uploadBandwidth;

    @Value("${bigfile.resource.bandwidth.download}")
    private long downloadBandwidth;

    @Value("${bigfile.resource.concurrent.upload}")
    private int uploadMaxThreadCount;

    @Value("${bigfile.resource.concurrent.download}")
    private int downloadMaxThreadCount;

    @Value("${bigfile.resource.single.concurrent.upload}")
    private int uploadMaxThreadCountPerFile;

    @Value("${bigfile.resource.single.concurrent.download}")
    private int downloadMaxThreadCountPerFile;

    private RateLimiter globalUploadRateLimiter;

    private RateLimiter globalDownloadRateLimiter;

    private AtomicInteger allocatedDownloadThreadCount = new AtomicInteger(0);

    private AtomicInteger allocatedUploadThreadCount = new AtomicInteger(0);

    private AtomicInteger downloadThreadCount = new AtomicInteger(0);

    private AtomicInteger uploadThreadCount = new AtomicInteger(0);

    private final Map<String, TransferStatus> uploadControlMap = new ConcurrentHashMap<>();

    private final Map<String, TransferStatus> downloadControlMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        this.globalDownloadRateLimiter = RateLimiterFactory.getRateLimiter(this.downloadBandwidth);
        this.globalUploadRateLimiter = RateLimiterFactory.getRateLimiter(this.uploadBandwidth);
    }

    @Override
    public RateLimiter getGlobalUploadRateLimiter() {
        return this.globalUploadRateLimiter;
    }

    @Override
    public RateLimiter getGlobalDownloadRateLimiter() {
        return this.globalDownloadRateLimiter;
    }

    @Override
    public void startDownload(String fileId) {
        downloadThreadCount.incrementAndGet();
        allocatedDownloadThreadCount.decrementAndGet();
        TransferStatus status = downloadControlMap.get(fileId);
        status.concurrency.incrementAndGet();
        status.latestUpdate = new Date().getTime();
    }

    @Override
    public void startUpload(String fileId) {
        uploadThreadCount.incrementAndGet();
        allocatedUploadThreadCount.decrementAndGet();
        TransferStatus status = downloadControlMap.get(fileId);
        status.concurrency.incrementAndGet();
        status.latestUpdate = new Date().getTime();
    }

    @Override
    public void endDownload(String fileId) {
        downloadThreadCount.decrementAndGet();
        allocatedDownloadThreadCount.incrementAndGet();
        TransferStatus status = downloadControlMap.get(fileId);
        status.concurrency.decrementAndGet();
        status.latestUpdate = new Date().getTime();
    }

    @Override
    public void endUpload(String fileId) {
        uploadThreadCount.decrementAndGet();
        allocatedUploadThreadCount.incrementAndGet();
        TransferStatus status = downloadControlMap.get(fileId);
        status.concurrency.decrementAndGet();
        status.latestUpdate = new Date().getTime();
    }

    private int getMaxDownloadAvailableThreadCount() {
        return downloadMaxThreadCount - downloadThreadCount.get() - allocatedDownloadThreadCount.get();
    }

    private int getDownloadAvailableThreadCount() { // 不算 10% 预留空间的情况下，最多能拿到多少线程
        return (int) (downloadMaxThreadCount * 0.9) - downloadThreadCount.get() - allocatedDownloadThreadCount.get();
    }

    private int getMaxUploadAvailableThreadCount() {
        return uploadMaxThreadCount - uploadThreadCount.get() - allocatedUploadThreadCount.get();
    }

    private int getUploadAvailableThreadCount() { // 不算 10% 预留空间的情况下，最多能拿到多少线程
        return (int) (uploadMaxThreadCount * 0.9) - uploadThreadCount.get() - allocatedUploadThreadCount.get();
    }

    @Override
    public int allocateDownloadThreads(String fileId) {
        int maxAvailableThreadCount = getMaxDownloadAvailableThreadCount();
        if (maxAvailableThreadCount <= 0) { // 完全分配不了线程了。
            throw new BigFileTooMuchConcurrentException();
        }
        int availableThreadCount = getDownloadAvailableThreadCount();
        int maxConcurrency = (int) Math.max(1, Math.floor((downloadMaxThreadCount) * 0.9 / downloadControlMap.size()));
        if (maxConcurrency > downloadMaxThreadCountPerFile) { // 单个文件传输线程不能超过单文件最大线程数
            maxConcurrency = downloadMaxThreadCountPerFile;
        }
        if (maxConcurrency > availableThreadCount) {
            maxConcurrency = availableThreadCount;
        }
        if (maxConcurrency < 1) {
            maxConcurrency = 1; // 最少保证分配一个
        }

        TransferStatus status = new TransferStatus();
        status.concurrency = new AtomicInteger(0);
        status.maxConcurrency = maxConcurrency;
        downloadControlMap.put(fileId, status);
        allocatedDownloadThreadCount.addAndGet(status.maxConcurrency);
        status.latestUpdate = new Date().getTime();
        return maxConcurrency;
    }

    @Override
    public int allocateUploadThreads(String fileId) {
        int maxAvailableThreadCount = getMaxUploadAvailableThreadCount();
        if (maxAvailableThreadCount <= 0) { // 完全分配不了线程了。
            throw new BigFileTooMuchConcurrentException();
        }
        int availableThreadCount = getUploadAvailableThreadCount();
        int maxConcurrency = (int) Math.max(1, Math.floor((uploadMaxThreadCount) * 0.9 / uploadControlMap.size()));
        if (maxConcurrency > uploadMaxThreadCountPerFile) { // 单个文件传输线程不能超过单文件最大线程数
            maxConcurrency = uploadMaxThreadCountPerFile;
        }
        if (maxConcurrency > availableThreadCount) {
            maxConcurrency = availableThreadCount;
        }
        if (maxConcurrency < 1) {
            maxConcurrency = 1; // 最少保证分配一个
        }

        TransferStatus status = new TransferStatus();
        status.concurrency = new AtomicInteger(0);
        status.maxConcurrency = maxConcurrency;
        uploadControlMap.put(fileId, status);
        allocatedUploadThreadCount.addAndGet(status.maxConcurrency);
        status.latestUpdate = new Date().getTime();
        return maxConcurrency;
    }

    @Override
    public int getAllowedDownloadThreads(String fileId) {
        int maxConcurrency = (int) Math.max(1, Math.floor((downloadMaxThreadCount) * 0.9 / downloadControlMap.size()));
        if (maxConcurrency > downloadMaxThreadCountPerFile) { // 单个文件传输线程不能超过单文件最大线程数
            maxConcurrency = downloadMaxThreadCountPerFile;
        }
        TransferStatus status = downloadControlMap.get(fileId);
        int preMaxConcurrency = status.maxConcurrency;
        if (maxConcurrency >= preMaxConcurrency) { // 增多了，可以多分配线程
            // 判断是否超了总线程数
            int availableThreadCount = getDownloadAvailableThreadCount() + preMaxConcurrency;
            status.maxConcurrency = Math.min(Math.max(availableThreadCount, status.concurrency.get()), maxConcurrency);
        } else { // 减少了，最多减少到当前并发数
            status.maxConcurrency = Math.max(status.concurrency.get(), maxConcurrency);
        }
        allocatedDownloadThreadCount.addAndGet(status.maxConcurrency - preMaxConcurrency); // 修改系统预留线程数
        status.latestUpdate = new Date().getTime();
        return maxConcurrency;
    }

    @Override
    public int getAllowedUploadThreads(String fileId) {
        int maxConcurrency = (int) Math.max(1, Math.floor((uploadMaxThreadCount) * 0.9 / uploadControlMap.size()));
        if (maxConcurrency > uploadMaxThreadCountPerFile) { // 单个文件传输线程不能超过单文件最大线程数
            maxConcurrency = uploadMaxThreadCountPerFile;
        }
        TransferStatus status = uploadControlMap.get(fileId);
        int preMaxConcurrency = status.maxConcurrency;
        if (maxConcurrency >= preMaxConcurrency) { // 增多了，可以多分配线程
            // 判断是否超了总线程数
            int availableThreadCount = getUploadAvailableThreadCount() + preMaxConcurrency;
            status.maxConcurrency = Math.min(Math.max(availableThreadCount, status.concurrency.get()), maxConcurrency);
        } else { // 减少了，最多减少到当前并发数
            status.maxConcurrency = Math.max(status.concurrency.get(), maxConcurrency);
        }
        allocatedUploadThreadCount.addAndGet(status.maxConcurrency - preMaxConcurrency); // 修改系统预留线程数
        status.latestUpdate = new Date().getTime();
        return maxConcurrency;
    }

    private static class TransferStatus {

        // 并发的线程数量
        private AtomicInteger concurrency;

        // 预分配的并发线程数
        private int maxConcurrency;

        // 连接最后一次活跃的时间
        private long latestUpdate;


    }

    @Scheduled(fixedRate = 5000)
    private void clearUnActiveTransferStatus() {
        // 删除写请求
        Set<String> statusToRemoved = new HashSet<>();
        for (Map.Entry<String, TransferStatus> entry : this.downloadControlMap.entrySet()) {
            String fileId = entry.getKey();
            TransferStatus status = entry.getValue();
            if (status.concurrency.get() == 0 && new Date().getTime() - status.latestUpdate > 20000) {
                // 分配了线程但是不访问，辣鸡！
                statusToRemoved.add(fileId);
                allocatedDownloadThreadCount.addAndGet(status.maxConcurrency);
            }
        }
        for (String fileId : statusToRemoved) {
            downloadControlMap.remove(fileId);
        }
        statusToRemoved.clear();

        // 删除读请求
        for (Map.Entry<String, TransferStatus> entry : this.uploadControlMap.entrySet()) {
            String fileId = entry.getKey();
            TransferStatus status = entry.getValue();
            if (status.concurrency.get() == 0 && new Date().getTime() - status.latestUpdate > 20000) {
                // 分配了线程但是不访问，辣鸡！
                statusToRemoved.add(fileId);
                allocatedUploadThreadCount.addAndGet(status.maxConcurrency);
            }
        }
        for (String fileId : statusToRemoved) {
            uploadControlMap.remove(fileId);
        }
    }

}
