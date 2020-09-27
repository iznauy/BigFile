package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BigFileUnexpectedParamException extends BigFileRuntimeException {

    public BigFileUnexpectedParamException(String message) {
        super(message);
    }
}
