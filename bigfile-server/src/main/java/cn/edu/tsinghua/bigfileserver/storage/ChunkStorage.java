package cn.edu.tsinghua.bigfileserver.storage;

import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
public interface ChunkStorage {

    boolean hasExisted(String fileId, long chunkId) throws BigFileException;

    byte[] readChunk(String fileId, long chunkId, long begin, long size) throws BigFileException;

    int writeChunk(String fileId, byte[] chunk, long chunkId, long begin) throws BigFileException;

    boolean deleteChunk(String fileId, long chunkId) throws BigFileException;

}
