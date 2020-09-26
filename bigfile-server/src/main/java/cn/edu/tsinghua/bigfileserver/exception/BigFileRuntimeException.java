package cn.edu.tsinghua.bigfileserver.exception;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public class BigFileRuntimeException extends RuntimeException {

    public BigFileRuntimeException() {
    }

    public BigFileRuntimeException(String message) {
        super(message);
    }
}
