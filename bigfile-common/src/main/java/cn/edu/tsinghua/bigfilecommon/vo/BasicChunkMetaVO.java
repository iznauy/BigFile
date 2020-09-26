package cn.edu.tsinghua.bigfilecommon.vo;

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
public class BasicChunkMetaVO {

    private String fileId;

    private long chunkId;

    private long offset;

    private long length;

    private String checkSum;

}
