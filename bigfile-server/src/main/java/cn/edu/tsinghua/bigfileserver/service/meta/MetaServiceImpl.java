package cn.edu.tsinghua.bigfileserver.service.meta;

import cn.edu.tsinghua.bigfilecommon.vo.BasicChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import cn.edu.tsinghua.bigfilecore.entity.Meta;
import cn.edu.tsinghua.bigfileserver.dao.ChunkMetaDao;
import cn.edu.tsinghua.bigfileserver.dao.MetaDao;
import cn.edu.tsinghua.bigfileserver.exception.BigFileDuplicateCreationException;
import cn.edu.tsinghua.bigfileserver.exception.BigFileNotExistException;
import cn.edu.tsinghua.bigfileserver.po.ChunkMetaPO;
import cn.edu.tsinghua.bigfileserver.po.MetaPO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Service
public class MetaServiceImpl implements MetaService {

    @Value("${bigfile.chunk.size}")
    private long chunkSize;

    @Value("${bigfile.chunk.checkSumType}")
    private CheckSumType checkSumType;

    @Value("${bigfile.chunk.compressionType}")
    private CompressionType compressionType;

    private MetaDao metaDao;

    private ChunkMetaDao chunkMetaDao;

    @Override
    public BasicMetaVO createMeta(String fileId, long size) {
        log.info("[createMeta]: fileId={}, size={}", fileId, size);
        MetaPO metaPO = metaDao.getMetaByFileId(fileId);
        if (metaPO != null) {
            log.info("[createMeta] file {} exists: {}", fileId, metaPO);
            throw new BigFileDuplicateCreationException(fileId);
        }
        metaPO = new MetaPO(fileId, size, chunkSize, (long) Math.ceil(size * 1.0 / chunkSize),
                size == 0, checkSumType, compressionType);
        metaDao.saveMeta(metaPO);
        return new BasicMetaVO(chunkSize, checkSumType, compressionType);
    }

    @Override
    public MetaVO getMeta(String fileId) {
        return MetaPO.toVO(metaDao.getMetaByFileId(fileId));
    }

    @Override
    public ChunkMetaVO getChunkMeta(String fileId, long chunkId) {
        return ChunkMetaPO.toVO(chunkMetaDao.getChunkMeta(fileId, chunkId));
    }

    @Override
    public List<ChunkMetaVO> getChunkMetaList(String fileId) {
        return chunkMetaDao.getChunkMetaList(fileId).stream().map(ChunkMetaPO::toVO).collect(Collectors.toList());
    }

    // TODO: 开启事务
    @Override
    public void uploadChunkMetaList(List<BasicChunkMetaVO> chunkMetaVOList) {
        String fileId = chunkMetaVOList.get(0).getFileId();
        MetaPO meta = metaDao.getMetaByFileId(fileId);
        if (meta == null) {
            throw new BigFileNotExistException(fileId);
        }
        meta.setUploadChunkMetaList(true);
        metaDao.saveMeta(meta);
        chunkMetaDao.saveChunkMetas(chunkMetaVOList.stream().map(ChunkMetaPO::fromBasicVO).collect(Collectors.toList()));
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
