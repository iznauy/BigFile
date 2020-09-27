package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BigFileRuntimeException extends RuntimeException {

    public BigFileRuntimeException() {
    }

    public BigFileRuntimeException(String message) {
        super(message);
    }

    public BigFileRuntimeException(Throwable cause) {
        super(cause);
    }
}
