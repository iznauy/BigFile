package cn.edu.tsinghua.bigfilecore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    /**
     * 文件的 id
     */
    private String fileId;

    /**
     * 文件的总大小
     */
    private long size;

    /**
     * 每个文件块的大小（压缩前）
     */
    private long chunkSize;

    /**
     * 还有多少文件块没有传输到服务器，
     * 在 uploadChunkMetaList = false 时，该字段为 0
     * 在 uploadChunkMetaList = true 时，如果该字段为 0，则意味着文件已经被完整地传输到服务器上。
     */
    private long chunksToUpload;

    /**
     * 客户端是否已经上传文件块元信息列表
     */
    private boolean uploadChunkMetaList = false;

    /**
     * 文件块采用的校验和计算方法
     */
    private CheckSumType checkSumType;

    /**
     * 文件块采用的数据压缩方法
     */
    private CompressionType compressionType;

}
