package cn.edu.tsinghua.bigfileclient.download.entity;

import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    private long chunkSize;

    private CheckSumType checkSumType;

    private CompressionType compressionType;

}
