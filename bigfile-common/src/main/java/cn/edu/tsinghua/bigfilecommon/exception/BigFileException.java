package cn.edu.tsinghua.bigfilecommon.exception;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public class BigFileException extends Exception {

    public BigFileException() {
    }

    public BigFileException(String message) {
        super(message);
    }

    public BigFileException(Throwable cause) {
        super(cause);
    }
}
