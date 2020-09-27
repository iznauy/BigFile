package cn.edu.tsinghua.bigfileserver.service.meta;

import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import cn.edu.tsinghua.bigfileserver.dao.ChunkMetaDao;
import cn.edu.tsinghua.bigfileserver.dao.MetaDao;
import cn.edu.tsinghua.bigfileserver.exception.BigFileDuplicateCreationException;
import cn.edu.tsinghua.bigfileserver.po.MetaPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${bigfile.chunk.size}")
    private long chunkSize;

    @Value("${bigfile.chunk.checkSumType}")
    private CheckSumType checkSumType;

    @Value("${bigfile.chunk.compressionType}")
    private CompressionType compressionType;

    private MetaDao metaDao;

    private ChunkMetaDao chunkMetaDao;

    @Override
    public BasicMetaVO createFile(String id, long size) {
        logger.info("[createFile]: id={}, size={}", id, size);
        MetaPO metaPO = metaDao.getMetaByFileId(id);
        if (metaPO != null) {
            logger.info("[createFile] file {} exists: {}", id, metaPO);
            throw new BigFileDuplicateCreationException(id);
        }
        metaPO = new MetaPO(id, size, chunkSize, (long) Math.ceil(size * 1.0 / chunkSize),
                false, checkSumType, compressionType);
        metaDao.saveMeta(metaPO);
        return new BasicMetaVO(chunkSize, checkSumType, compressionType);
    }

    @Autowired
    public void setMetaDao(MetaDao metaDao) {
        this.metaDao = metaDao;
    }

    @Autowired
    public void setChunkMetaDao(ChunkMetaDao chunkMetaDao) {
        this.chunkMetaDao = chunkMetaDao;
    }
}
