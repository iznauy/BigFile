package cn.edu.tsinghua.bigfileserver.service.meta;

import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@Service
public class MetaServiceImpl implements MetaService {

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceImpl.class);

    @Override
    public BasicMetaVO createFile(String id, long size) {
        logger.info(String.format("create file: id=%s, size=%d", id, size));
        return new BasicMetaVO(65536, CheckSumType.MD5, CompressionType.NONE);
    }
}
