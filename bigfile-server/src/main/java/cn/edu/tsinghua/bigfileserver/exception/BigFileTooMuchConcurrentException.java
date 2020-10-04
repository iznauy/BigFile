package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-10-04.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class BigFileTooMuchConcurrentException extends BigFileRuntimeException {
}
