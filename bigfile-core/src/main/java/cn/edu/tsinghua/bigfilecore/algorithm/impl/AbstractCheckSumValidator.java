package cn.edu.tsinghua.bigfilecore.algorithm.impl;

import cn.edu.tsinghua.bigfilecore.algorithm.CheckSumValidator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public abstract class AbstractCheckSumValidator implements CheckSumValidator {

    protected abstract String getAlgorithm();

    @Override
    public String calculateCheckSum(byte[] chunkData) {
        try {
            MessageDigest m = MessageDigest.getInstance(getAlgorithm());
            m.update(chunkData);
            byte[] s = m.digest();
            StringBuilder resultBuilder = new StringBuilder();
            for (byte c: s) {
                resultBuilder.append(Integer.toHexString((0x000000FF & c) | 0xFFFFFF00).substring(6));
            }
            return resultBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean checkChunk(byte[] chunkData, String checkSum) {
        return checkSum.equals(calculateCheckSum(chunkData));
    }
}
