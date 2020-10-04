package cn.edu.tsinghua.bigfileserver.dao.daoImpl;

import cn.edu.tsinghua.bigfileserver.dao.ChunkMetaDao;
import cn.edu.tsinghua.bigfileserver.po.ChunkMetaPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Repository
public class ChunkMetaDaoImpl implements ChunkMetaDao {

    private ChunkMetaRepository chunkMetaRepository;

    @Override
    public void saveChunkMeta(ChunkMetaPO chunkMetaPO) {
        chunkMetaRepository.save(chunkMetaPO);
    }

    @Override
    public void saveChunkMetas(Collection<ChunkMetaPO> chunkMetaPOs) {
        chunkMetaRepository.saveAll(chunkMetaPOs);
    }

    @Override
    public List<ChunkMetaPO> getChunkMetaList(String fileId) {
        return chunkMetaRepository.findAllByFileId(fileId);
    }

    @Override
    public ChunkMetaPO getChunkMeta(String fileId, long chunkId) {
        return chunkMetaRepository.findFirstByFileIdAndChunkId(fileId, chunkId);
    }

    @Autowired
    public void setChunkMetaRepository(ChunkMetaRepository chunkMetaRepository) {
        this.chunkMetaRepository = chunkMetaRepository;
    }
}
