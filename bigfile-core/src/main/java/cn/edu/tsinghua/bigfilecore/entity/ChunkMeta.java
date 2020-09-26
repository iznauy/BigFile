package cn.edu.tsinghua.bigfilecore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkMeta {

    /**
     * 文件块对应的原始文件的 id
     */
    private String fileId;

    /**
     * 文件块的 id
     */
    private long chunkId;

    /**
     * 文件块在原始文件中对应的偏移量
     */
    private long offset;

    /**
     * 压缩后的数据的长度
     */
    private long length;

    /**
     * 数据校验和
     */
    private String checkSum = "";

    /**
     * 目前有多少数据存储在服务端
     */
    private long size;

}
