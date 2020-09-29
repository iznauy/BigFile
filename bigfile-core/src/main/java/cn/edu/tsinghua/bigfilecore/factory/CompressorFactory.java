package cn.edu.tsinghua.bigfilecore.factory;

import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;

import java.util.Arrays;

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
        switch (compressionType) {
            case NONE:
                return NoneCompressor.getInstance();
            default:
                return null;
        }
    }

    private static class NoneCompressor implements Compressor {

        private static NoneCompressor instance = new NoneCompressor();

        private NoneCompressor() {

        }

        @Override
        public byte[] compressChunkData(byte[] data) {
            return Arrays.copyOf(data, data.length);
        }

        @Override
        public byte[] decompressChunkData(byte[] data) {
            return Arrays.copyOf(data, data.length);
        }

        static NoneCompressor getInstance() {
            return instance;
        }

    }

}
