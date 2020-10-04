package cn.edu.tsinghua.bigfileserver.dao.daoImpl;

import cn.edu.tsinghua.bigfileserver.po.ChunkMetaPO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public interface ChunkMetaRepository extends CrudRepository<ChunkMetaPO, Long> {

    List<ChunkMetaPO> findAllByFileId(String fileId);

    ChunkMetaPO findFirstByFileIdAndChunkId(String fileId, long chunkId);

}
