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
public class BigFileNotExistException extends BigFileRuntimeException {

    private static final String MESSAGE_TEMPLATE = "File %s is not exists.";

    public BigFileNotExistException(String fileId) {
        super(String.format(MESSAGE_TEMPLATE, fileId));
    }

}
