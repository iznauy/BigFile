package cn.edu.tsinghua.bigfilecommon.vo;

import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
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
public class MetaVO {

    /**
     * 传输是否完成
     */
    private boolean uploadFinished;

    /**
     * 客户端是否上传文件块元信息列表
     */
    private boolean uploadChunkMetaList;

    private long chunkSize;

    private CheckSumType checkSumType;

    private CompressionType compressionType;

}
