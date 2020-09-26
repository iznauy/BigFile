package cn.edu.tsinghua.bigfileserver.service.storage;

import cn.edu.tsinghua.bigfileserver.exception.BigFileException;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public interface ChunkStorage {

    boolean hasExisted(String fileId, long chunkId) throws BigFileException;

    byte[] readChunk(String id, long chunkId, long begin, long size) throws BigFileException;

    boolean writeChunk(String id, byte[] chunk, long chunkId, long begin) throws BigFileException;

    boolean deleteChunk(String id, long chunkId) throws BigFileException;

}
