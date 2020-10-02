package cn.edu.tsinghua.bigfilecore.algorithm.impl;

import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public class SnappyCompressor implements Compressor {

    private static SnappyCompressor instance = new SnappyCompressor();

    private SnappyCompressor() {

    }

    @Override
    public byte[] compressChunkData(byte[] data) {
        try {
            return Snappy.compress(data);
        } catch (IOException e) {
            e.printStackTrace();
            return data;
        }
    }

    @Override
    public byte[] decompressChunkData(byte[] data) {
        try {
            return Snappy.uncompress(data);
        } catch (IOException e) {
            e.printStackTrace();
            return data;
        }
    }

    public static SnappyCompressor getInstance() {
        return instance;
    }

}
