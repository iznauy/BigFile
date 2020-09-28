package cn.edu.tsinghua.bigfileclient.upload.entity;

import cn.edu.tsinghua.bigfilecommon.vo.BasicChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkMeta {

    private String fileId;

    private long chunkId;

    private long offset;

    private long length;

    private String checkSum;

    /**
     * 有多少数据存储在服务器上
     */
    private long size;

    public boolean isUploadFinished() {
        return length == size;
    }

    public static BasicChunkMetaVO toBasicVO(ChunkMeta chunkMeta) {
        if (chunkMeta == null) {
            return null;
        }
        return new BasicChunkMetaVO(chunkMeta.fileId, chunkMeta.chunkId, chunkMeta.offset, chunkMeta.length, chunkMeta.checkSum);
    }

    public static ChunkMetaVO toVO(ChunkMeta chunkMeta) {
        if (chunkMeta == null) {
            return null;
        }
        return new ChunkMetaVO(chunkMeta.fileId, chunkMeta.chunkId, chunkMeta.offset, chunkMeta.length, chunkMeta.checkSum, chunkMeta.size);
    }

    public static ChunkMeta fromVO(ChunkMetaVO chunkMetaVO) {
        if (chunkMetaVO == null) {
            return null;
        }
        return new ChunkMeta(chunkMetaVO.getFileId(), chunkMetaVO.getChunkId(), chunkMetaVO.getOffset(), chunkMetaVO.getLength(),
                chunkMetaVO.getCheckSum(), chunkMetaVO.getSize());
    }


}
