package cn.edu.tsinghua.bigfilecore.algorithm.impl;

import sun.security.provider.SHA;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public class SHA256CheckSumValidator extends AbstractCheckSumValidator {

    private static SHA256CheckSumValidator instance = new SHA256CheckSumValidator();

    private SHA256CheckSumValidator() {

    }

    @Override
    protected String getAlgorithm() {
        return "SHA-256";
    }

    public static SHA256CheckSumValidator getInstance() {
        return instance;
    }

}
