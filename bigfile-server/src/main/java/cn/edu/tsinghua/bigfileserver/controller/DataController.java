package cn.edu.tsinghua.bigfileserver.controller;

import cn.edu.tsinghua.bigfilecommon.vo.ChunkTransferVO;
import cn.edu.tsinghua.bigfileserver.exception.BigFileIOException;
import cn.edu.tsinghua.bigfileserver.service.data.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Controller
public class DataController {

    private DataService dataService;

    @PostMapping("/chunk/data/")
    @ResponseStatus(HttpStatus.OK)
    public void uploadChunk(@RequestParam String fileId, @RequestParam long chunkId,
                            @RequestParam long begin, @RequestParam long size,
                            HttpServletRequest request) {
        byte[] data = new byte[(int)size];
        int length = 0;
        try (BufferedInputStream is = new BufferedInputStream(request.getInputStream())) {
            length = is.read(data);
        } catch (IOException e) {
            log.error("uploadChunk error: {}", e.getMessage());
            throw new BigFileIOException(e);
        }
        if (size != length) {
            log.warn("length != size: length={}, size={}", length, size);
            size = length;
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
        try (BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            os.write(data);
            os.flush();
        } catch (IOException e) {
            log.error("getChunk error: {}", e.getMessage());
            throw new BigFileIOException(e);
        }

    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
