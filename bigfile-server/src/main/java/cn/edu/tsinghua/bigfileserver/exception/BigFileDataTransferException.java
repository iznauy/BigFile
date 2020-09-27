package cn.edu.tsinghua.bigfileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BigFileDataTransferException extends BigFileRuntimeException {

    private static final String MESSAGE_TEMPLATE = "Chunk %d of file %s got error in transfer.";

    public BigFileDataTransferException(String fileId, long chunkId) {
        super(String.format(MESSAGE_TEMPLATE, chunkId, fileId));
    }

}
