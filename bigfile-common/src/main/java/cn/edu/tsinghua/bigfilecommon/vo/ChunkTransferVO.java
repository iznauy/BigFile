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
public class ChunkTransferVO {

    private String fileId;

    private long chunkId;

    /**
     * 从当前文件块的哪个位置开始传输
     */
    private long begin;

    /**
     * 当前传输的数据块大小
     */
    private long size;

}
