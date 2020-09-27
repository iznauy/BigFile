package cn.edu.tsinghua.bigfileserver.po;

import cn.edu.tsinghua.bigfilecommon.vo.BasicChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecore.entity.ChunkMeta;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
@Table(name = "ChunkMeta")
@Entity
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChunkMetaPO {

    /**
     * 数据库自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * 文件块对应的原始文件的 id
     */
    @Column(nullable = false)
    private String fileId;

    /**
     * 文件块的 id
     */
    @Column(nullable = false)
    private long chunkId;

    /**
     * 文件块在原始文件中对应的偏移量
     */
    @Column(nullable = false)
    private long offset;

    /**
     * 压缩后的数据的长度
     */
    @Column(nullable = false)
    private long length;

    /**
     * 数据校验和
     */
    @Column
    private String checkSum;

    /**
     * 目前有多少数据存储在服务端
     */
    @Column(nullable = false)
    private long size;

    public ChunkMetaPO(String fileId, long chunkId, long offset, long length, String checkSum, long size) {
        this.fileId = fileId;
        this.chunkId = chunkId;
        this.offset = offset;
        this.length = length;
        this.checkSum = checkSum;
        this.size = size;
    }

    public static ChunkMetaVO toVO(ChunkMetaPO po) {
        if (po == null) {
            return null;
        }
        return new ChunkMetaVO(po.fileId, po.chunkId, po.offset, po.length, po.checkSum, po.size);
    }

    public static ChunkMetaPO fromBasicVO(BasicChunkMetaVO vo) {
        if (vo == null) {
            return null;
        }
        return new ChunkMetaPO(vo.getFileId(), vo.getChunkId(), vo.getOffset(), vo.getLength(), vo.getCheckSum(), 0);
    }

}
