package cn.edu.tsinghua.bigfileserver.service.storage;

import cn.edu.tsinghua.bigfileserver.exception.BigFileException;
import cn.edu.tsinghua.bigfileserver.exception.BigFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@Service
public class FileChunkStorageImpl implements ChunkStorage {

    private static final Logger logger = LoggerFactory.getLogger(FileChunkStorageImpl.class);


    private static final String CHUNK_NAME_TEMPLATE = "%s.%d";

    @Value("${bigfile.chunk.storage.basedir}")
    private String baseDir;

    @PostConstruct
    public void init() {
        if (baseDir == null) {
            baseDir = "data/";
        }
        File dataDir = new File(baseDir);
        if (dataDir.exists()) {
            return;
        }
        if (dataDir.mkdirs()) {
            return;
        }
        logger.error("create file chunk storage baseDir " + baseDir + " failure");
    }

    private File getFile(String fileId, long chunkId) {
        return new File(baseDir, String.format(CHUNK_NAME_TEMPLATE, fileId, chunkId));
    }


    @Override
    public boolean hasExisted(String fileId, long chunkId) throws BigFileException {
        File file = getFile(fileId, chunkId);
        return file.exists();
    }

    @Override
    public byte[] readChunk(String fileId, long chunkId, long begin, long size) throws BigFileException {
        File file = getFile(fileId, chunkId);
        if (!file.exists()) {
            throw new BigFileNotFoundException(fileId, chunkId);
        }

        return new byte[0];
    }

    @Override
    public boolean writeChunk(String fileId, byte[] chunk, long chunkId, long begin) throws BigFileException {
        return false;
    }

    @Override
    public boolean deleteChunk(String fileId, long chunkId) throws BigFileException {
        return false;
    }
}