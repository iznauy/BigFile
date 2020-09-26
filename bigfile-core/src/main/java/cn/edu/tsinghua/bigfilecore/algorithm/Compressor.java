package cn.edu.tsinghua.bigfilecore.algorithm;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
public interface Compressor {

    byte[] compressChunkData(byte[] data);

    byte[] decompressChunkData(byte[] data);

}
