package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BigFileChunkCheckSumException extends BigFileRuntimeException {

    public BigFileChunkCheckSumException() {
    }

    public BigFileChunkCheckSumException(String message) {
        super(message);
    }
}
