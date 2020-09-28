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
        switch (checkSumType) {
            case NONE:
                return NoneCheckSumValidator.getInstance();
            default:
                return null;
        }
    }

    private static class NoneCheckSumValidator implements CheckSumValidator {

        private static NoneCheckSumValidator instance = new NoneCheckSumValidator();

        private NoneCheckSumValidator() {

        }

        @Override
        public String calculateCheckSum(byte[] chunkData) {
            return "2333";
        }

        @Override
        public boolean checkChunk(byte[] chunkData, String checkSum) {
            return checkSum.equals("2333");
        }

        static NoneCheckSumValidator getInstance() {
            return instance;
        }


    }

}
