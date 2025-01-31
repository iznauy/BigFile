package cn.edu.tsinghua.bigfilecommon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkMetaVO {

    private String fileId;

    private long chunkId;

    private long offset;

    private long length;

    private String checkSum;

    /**
     * 当前服务端存储了该文件块前多少字节的数据
     */
    private long size;

}
