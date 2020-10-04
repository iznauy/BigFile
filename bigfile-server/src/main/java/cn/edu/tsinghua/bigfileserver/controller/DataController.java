package cn.edu.tsinghua.bigfileserver.controller;

import cn.edu.tsinghua.bigfilecommon.vo.ChunkTransferVO;
import cn.edu.tsinghua.bigfilecore.algorithm.RateLimiter;
import cn.edu.tsinghua.bigfileserver.exception.BigFileIOException;
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

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Controller
public class DataController {

    private static int BlockSize = 100 * 1024;

    private DataService dataService;

    private ConcurrentControlService concurrentControlService;

    @PostMapping("/chunk/data/")
    @ResponseStatus(HttpStatus.OK)
    public void uploadChunk(@RequestParam String fileId, @RequestParam long chunkId,
                            @RequestParam long begin, @RequestParam long size,
                            HttpServletRequest request) {
        byte[] data = new byte[(int) size];
        int length = 0 , offset = 0;
        RateLimiter rateLimiter = concurrentControlService.getGlobalUploadRateLimiter();
        try (BufferedInputStream is = new BufferedInputStream(request.getInputStream())) {
            concurrentControlService.startUpload(fileId);
            int blockSize = Math.min(BlockSize, (int) (size - offset));
            while (offset < size && rateLimiter.acquire(blockSize) &&(length = is.read(data, offset, blockSize)) != -1) {
                offset += length;
                blockSize = Math.min(BlockSize, (int) (size - offset));
            }
        } catch (Exception e) {
            log.error("uploadChunk error: {}", e.getMessage());
            throw new BigFileIOException(e);
        } finally {
            concurrentControlService.endUpload(fileId);
        }
        if (offset != size) {
            log.warn("offset != size: offset={}, size={}", offset, size);
            size = offset;
        }
        ChunkTransferVO transferVO = new ChunkTransferVO(fileId, chunkId, begin, size);
        dataService.uploadChunkData(transferVO, data);
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
            BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
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
            throw new BigFileIOException(e);
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
