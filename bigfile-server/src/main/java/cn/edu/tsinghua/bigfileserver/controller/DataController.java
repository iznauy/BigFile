package cn.edu.tsinghua.bigfileserver.controller;

import cn.edu.tsinghua.bigfilecommon.vo.ChunkTransferVO;
import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
import cn.edu.tsinghua.bigfileserver.service.concurrent.ConcurrentControlService;
import cn.edu.tsinghua.bigfileserver.service.data.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Controller
public class DataController {

    private static int BlockSize = 8 * 1024;

    private DataService dataService;

    private ConcurrentControlService concurrentControlService;

    @PostMapping("/chunk/data/")
    @ResponseStatus(HttpStatus.OK)
    public void uploadChunk(@RequestParam String fileId, @RequestParam long chunkId,
                            @RequestParam long begin, @RequestParam long size,
                            HttpServletRequest request) {
        log.info("开始上传文件：{}, 文件块：{}", fileId, chunkId);
        byte[] data = new byte[(int) size];
        int length = 0, offset = 0;
        RateLimiter rateLimiter = concurrentControlService.getGlobalUploadRateLimiter();
        try (BufferedInputStream is = new BufferedInputStream(request.getInputStream(), BlockSize)) {
            concurrentControlService.startUpload(fileId);
            int blockSize = Math.min(BlockSize, (int) (size - offset));
            while (offset < size && (length = is.read(data, offset, blockSize)) != -1) {
                offset += length;
                blockSize = Math.min(BlockSize, (int) (size - offset));
                rateLimiter.acquire(length);
            }
        } catch (Exception e) {
            log.error("uploadChunk error: {}", e.getMessage());
        } finally {
            concurrentControlService.endUpload(fileId);
        }
        if (offset != size) {
            log.warn("用户上传时发生崩溃，应当传输 {} 字节，实际传输 {} 字节", offset, size);
            size = offset;
        }
        ChunkTransferVO transferVO = new ChunkTransferVO(fileId, chunkId, begin, size);
        dataService.uploadChunkData(transferVO, Arrays.copyOf(data, (int) size));
        log.info("文件：{}, 文件块：{} 结束上传", fileId, chunkId);
    }

    @GetMapping("/chunk/data/")
    @ResponseStatus(HttpStatus.OK)
    public void getChunk(@RequestParam String fileId, @RequestParam long chunkId,
                         @RequestParam long begin, @RequestParam long size,
                         HttpServletResponse response) {
        ChunkTransferVO transferVO = new ChunkTransferVO(fileId, chunkId, begin, size);
        byte[] data = dataService.getChunkData(transferVO);
        RateLimiter rateLimiter = concurrentControlService.getGlobalUploadRateLimiter();
        try {
            concurrentControlService.startDownload(fileId);
            BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream(), BlockSize);
            int blockSize;
            int offset = 0, length = data.length;
            do {
                blockSize = Math.min(BlockSize, length - offset);
                rateLimiter.acquire(blockSize);
                os.write(data, offset, blockSize);
                offset += blockSize;
            } while (offset < length);
            os.flush();
        } catch (IOException e) {
            log.error("getChunk error: {}", e.getMessage());
        } finally {
            concurrentControlService.endDownload(fileId);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/concurrent/download/")
    @ResponseBody
    public int getDownloadConcurrency(@RequestParam String fileId, @RequestParam boolean allocate) {
        if (allocate) {
            return concurrentControlService.allocateDownloadThreads(fileId);
        }
        return concurrentControlService.getAllowedDownloadThreads(fileId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/concurrent/upload/")
    @ResponseBody
    public int getUploadConcurrency(@RequestParam String fileId, @RequestParam boolean allocate) {
        if (allocate) {
            return concurrentControlService.allocateUploadThreads(fileId);
        }
        return concurrentControlService.getAllowedUploadThreads(fileId);
    }


    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Autowired
    public void setConcurrentControlService(ConcurrentControlService concurrentControlService) {
        this.concurrentControlService = concurrentControlService;
    }

}
