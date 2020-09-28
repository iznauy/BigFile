package cn.edu.tsinghua.bigfilecore.algorithm;

/**
 * Created on 2020-09-24.
 * Description:
 *
 * @author iznauy
 */
public interface CheckSumValidator {

    String calculateCheckSum(byte[] chunkData);

    boolean checkChunk(byte[] chunkData, String checkSum);

}
