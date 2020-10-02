package cn.edu.tsinghua.bigfilecore.algorithm.impl;

/**
 * Created on 2020-10-02.
 * Description:
 *
 * @author iznauy
 */
public class MD5CheckSumValidator extends AbstractCheckSumValidator {

    private static MD5CheckSumValidator instance = new MD5CheckSumValidator();

    private MD5CheckSumValidator() {

    }

    @Override
    protected String getAlgorithm() {
        return "MD5";
    }

    public static MD5CheckSumValidator getInstance() {
        return instance;
    }

}
