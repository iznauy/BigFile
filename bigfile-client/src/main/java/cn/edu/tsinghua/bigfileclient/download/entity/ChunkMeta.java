package cn.edu.tsinghua.bigfileclient.download.entity;

import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkMeta {

    private long chunkId;

    private long offset;

    private long length;

    private String checkSum;

    /**
     * 有多少数据存储在本地
     */
    private long size;

    public boolean isDownloadFinish() {
        return size == length;
    }

    public static ChunkMeta fromVO(ChunkMetaVO chunkMetaVO) {
        if (chunkMetaVO == null) {
            return null;
        }
        return new ChunkMeta(chunkMetaVO.getChunkId(), chunkMetaVO.getOffset(),
                chunkMetaVO.getLength(), chunkMetaVO.getCheckSum(), 0);
    }

}
