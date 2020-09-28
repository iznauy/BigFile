package cn.edu.tsinghua.bigfileclient.upload.entity;

import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
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
@AllArgsConstructor
@NoArgsConstructor
public class Meta {

    private long size;

    private long chunkSize;

    private boolean hasUploadChunkMetaList;

    private CheckSumType checkSumType;

    private CompressionType compressionType;

}
