package cn.edu.tsinghua.bigfileserver.controller;

import cn.edu.tsinghua.bigfilecommon.vo.BasicChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import cn.edu.tsinghua.bigfileserver.service.meta.MetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@RestController
public class MetaController {

    private MetaService metaService;

    @PostMapping("/meta/")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicMetaVO createMeta(@RequestParam String fileId, @RequestParam long size) {
        return metaService.createMeta(fileId, size);
    }

    @GetMapping("/meta/")
    @ResponseStatus(HttpStatus.OK)
    public MetaVO getMeta(@RequestParam String fileId) {
        return metaService.getMeta(fileId);
    }

    @GetMapping("/chunk/meta/list/")
    @ResponseStatus(HttpStatus.OK)
    public List<ChunkMetaVO> getChunkMetaList(@RequestParam String fileId) {
        return metaService.getChunkMetaList(fileId);
    }

    @GetMapping("/chunk/meta/")
    public ChunkMetaVO getChunkMeta(@RequestParam String fileId, @RequestParam long chunkId) {
        return metaService.getChunkMeta(fileId, chunkId);
    }

    @PostMapping("/chunk/meta/list/")
    @ResponseStatus(HttpStatus.OK)
    public void uploadChunkMetaList(@RequestBody List<BasicChunkMetaVO> chunkMetaVOList) {
        metaService.uploadChunkMetaList(chunkMetaVOList);
    }

    @Autowired
    public void setMetaService(MetaService metaService) {
        this.metaService = metaService;
    }
}
