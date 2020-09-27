package cn.edu.tsinghua.bigfileserver.po;

import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
@Table(name = "Meta")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MetaPO {

    /**
     * 数据库自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * 文件的 id
     */
    @Column(nullable = false)
    private String fileId;

    /**
     * 文件的总大小
     */
    @Column(nullable = false)
    private long size;

    /**
     * 每个文件块的大小（压缩前）
     */
    @Column(nullable = false)
    private long chunkSize;

    /**
     * 还有多少文件块没有传输到服务器，
     * 如果该字段为 0，则意味着文件已经被完整地传输到服务器上。
     */
    @Column(nullable = false)
    private long chunksToUpload;

    /**
     * 客户端是否已完整上传文件块元信息列表
     */
    @Column(nullable = false)
    private boolean uploadChunkMetaList = false;

    /**
     * 文件块采用的校验和计算方法
     */
    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private CheckSumType checkSumType;

    /**
     * 文件块采用的数据压缩方法
     */
    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private CompressionType compressionType;

    public MetaPO(String fileId, long size, long chunkSize, long chunksToUpload, boolean uploadChunkMetaList, CheckSumType checkSumType, CompressionType compressionType) {
        this.fileId = fileId;
        this.size = size;
        this.chunkSize = chunkSize;
        this.chunksToUpload = chunksToUpload;
        this.uploadChunkMetaList = uploadChunkMetaList;
        this.checkSumType = checkSumType;
        this.compressionType = compressionType;
    }

    public static MetaVO toVO(MetaPO po) {
        if (po == null) {
            return null;
        }
        return new MetaVO(po.fileId, po.chunkSize, po.chunksToUpload == 0,
                po.uploadChunkMetaList, po.checkSumType, po.compressionType);
    }

}
