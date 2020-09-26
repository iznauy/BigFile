package cn.edu.tsinghua.bigfileserver.controller;

import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfileserver.service.meta.MetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
@RestController
public class MetaController {

    private static final Logger logger = LoggerFactory.getLogger(MetaController.class);

    private MetaService metaService;

    @GetMapping("/create/file/")
    @ResponseStatus(HttpStatus.CREATED)
    public BasicMetaVO createFile(@RequestParam String id, @RequestParam long size) {
        return metaService.createFile(id, size);
    }

    @Autowired
    public void setMetaService(MetaService metaService) {
        this.metaService = metaService;
    }
}
