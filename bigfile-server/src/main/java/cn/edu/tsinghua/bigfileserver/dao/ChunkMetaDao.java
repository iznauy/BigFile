package cn.edu.tsinghua.bigfileserver.dao;

import cn.edu.tsinghua.bigfilecore.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileserver.po.ChunkMetaPO;

import java.util.Collection;
import java.util.List;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
public interface ChunkMetaDao {

    void saveChunkMeta(ChunkMetaPO chunkMetaPO);

    void saveChunkMetas(Collection<ChunkMetaPO> chunkMetaPOs);

    List<ChunkMetaPO> getChunkMetaList(String fileId);

    ChunkMetaPO getChunkMeta(String fileId, long chunkId);

}
