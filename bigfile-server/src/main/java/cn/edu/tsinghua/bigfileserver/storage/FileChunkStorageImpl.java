package cn.edu.tsinghua.bigfileserver.storage;

import cn.edu.tsinghua.bigfileserver.exception.BigFileException;
import cn.edu.tsinghua.bigfileserver.exception.BigFileChunkDataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.RandomAccess;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Component
public class FileChunkStorageImpl implements ChunkStorage {

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
        log.error("create file chunk storage baseDir " + baseDir + " failure");
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
            throw new BigFileChunkDataNotFoundException(fileId, chunkId);
        }
        RandomAccessFile f = null;
        FileChannel channel = null;
        byte[] data = new byte[(int)size];
        int offset = 0;
        try {
            f = new RandomAccessFile(file, "r");
            f.seek(begin);
            channel = f.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len;
            while ((len = channel.read(buffer)) != -1) {
                buffer.flip();
                buffer.get(data, offset, len);
                offset += len;
                buffer.clear();
            }
        } catch (IOException e) {
            log.error("read file channel error: {}", e.getMessage());
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (f != null) {
                    f.close();
                }
            } catch (IOException e) {
                log.error("create file channel or random access file error: {}", e.getMessage());
            }
        }
        if (offset >= size) {
            return data;
        }
        return Arrays.copyOf(data, offset);
    }

    @Override
    public int writeChunk(String fileId, byte[] chunk, long chunkId, long begin) throws BigFileException {
        File file = getFile(fileId, chunkId);
        try {
            if (!file.exists()) {
                boolean success = file.createNewFile();
                if (!success) {
                    return 0;
                }
            }
        } catch (IOException e) {
            log.warn("[writeChunk] create file failure: " + e.getMessage());
            return 0;
        }
        RandomAccessFile f = null;
        FileChannel channel = null;
        int offset = 0;
        int batchSize = 1024;
        try {
            f = new RandomAccessFile(file, "rw");
            f.seek(begin);
            channel = f.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(batchSize);
            while (offset < chunk.length) {
                if (offset + batchSize > chunk.length) {
                    batchSize = chunk.length - offset;
                }
                buffer.clear();
                buffer.put(chunk, offset, batchSize);
                buffer.flip();
                offset += channel.write(buffer);
            }
        } catch (IOException e) {
            log.error("write file channel error: {}", e.getMessage());
        }
        return offset;
    }

    @Override
    public boolean deleteChunk(String fileId, long chunkId) throws BigFileException {
        File file = getFile(fileId, chunkId);
        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }
}