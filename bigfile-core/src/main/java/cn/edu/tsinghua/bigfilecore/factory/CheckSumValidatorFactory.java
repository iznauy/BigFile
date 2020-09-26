package cn.edu.tsinghua.bigfilecore.factory;

import cn.edu.tsinghua.bigfilecore.algorithm.CheckSumValidator;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public class CheckSumValidatorFactory {

    private CheckSumValidatorFactory() {

    }

    public static CheckSumValidator getCheckSumValidator(CheckSumType checkSumType) {
        return null;
    }

}
