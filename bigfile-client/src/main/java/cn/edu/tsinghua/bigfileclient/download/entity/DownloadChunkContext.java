package cn.edu.tsinghua.bigfileclient.download.entity;

import cn.edu.tsinghua.bigfileclient.download.ChunkFile;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import lombok.Data;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@Data
public class DownloadChunkContext {

    private DownloadContext context;

    private ChunkMeta chunkMeta;

    private ChunkFile chunkFile;

    public DownloadChunkContext(DownloadContext context, ChunkMeta chunkMeta, ChunkFile chunkFile) {
        this.context = context;
        this.chunkMeta = chunkMeta;
        this.chunkFile = chunkFile;
    }

    public String getIp() {
        return context.getIp();
    }

    public int getPort() {
        return context.getPort();
    }

    public String getFileId() {
        return context.getFileId();
    }

    public long getChunkId() {
        return this.chunkMeta.getChunkId();
    }

    public long getCurrentChunkSize() {
        return this.chunkFile.length();
    }

    public long getChunkSize() {
        return this.context.getMeta().getChunkSize();
    }

    public CheckSumType getCheckSumType() {
        return this.context.getCheckSumType();
    }

    public String getCheckSum() {
        return this.chunkMeta.getCheckSum();
    }

}
