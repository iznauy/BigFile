package cn.edu.tsinghua.bigfilecore.factory;

import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public class CompressorFactory {

    private CompressorFactory() {

    }

    public static Compressor getCompressor(CompressionType compressionType) {
        return null;
    }

}
