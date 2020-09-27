package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BigFileNotFoundException extends BigFileRuntimeException {

    private static final String MESSAGE_TEMPLATE = "Chunk %d of file %s is not found.";

    public BigFileNotFoundException(String fileId, long chunkId) {
        super(String.format(MESSAGE_TEMPLATE, chunkId, fileId));
    }

}
