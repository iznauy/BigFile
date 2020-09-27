package cn.edu.tsinghua.bigfileserver.service.meta;

import cn.edu.tsinghua.bigfilecommon.vo.BasicChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;

import java.util.List;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public interface MetaService {

    BasicMetaVO createMeta(String fileId, long size);

    MetaVO getMeta(String fileId);

    ChunkMetaVO getChunkMeta(String fileId, long chunkId);

    List<ChunkMetaVO> getChunkMetaList(String fileId);

    void uploadChunkMetaList(List<BasicChunkMetaVO> chunkMetaVOList);

}
