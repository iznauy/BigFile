package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BigFileDuplicateCreationException extends BigFileRuntimeException {

    private static final String MESSAGE_TEMPLATE = "File %s has been created.";

    public BigFileDuplicateCreationException(String fileId) {
        super(String.format(MESSAGE_TEMPLATE, fileId));
    }
}
